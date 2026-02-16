package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.AuthResponse;
import com.truckbook.api.entity.AppUser;
import com.truckbook.api.entity.Organization;
import com.truckbook.api.entity.OtpRequest;
import com.truckbook.api.entity.Subscription;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.repository.AppUserRepository;
import com.truckbook.api.repository.OrganizationRepository;
import com.truckbook.api.repository.OtpRequestRepository;
import com.truckbook.api.repository.SubscriptionRepository;
import com.truckbook.api.security.JwtService;
import com.truckbook.api.service.AuthService;
import com.truckbook.api.service.OtpThrottleService;
import com.truckbook.api.exception.TooManyRequestsException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
  private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);

  private final OtpRequestRepository otpRequestRepository;
  private final AppUserRepository appUserRepository;
  private final OrganizationRepository organizationRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final JwtService jwtService;
  private final OtpThrottleService otpThrottleService;
  private final Environment environment;
  private final PasswordEncoder passwordEncoder;
  private static final String STATIC_OTP = "123456";

  public AuthServiceImpl(
      OtpRequestRepository otpRequestRepository,
      AppUserRepository appUserRepository,
      OrganizationRepository organizationRepository,
      SubscriptionRepository subscriptionRepository,
      JwtService jwtService,
      OtpThrottleService otpThrottleService,
      Environment environment,
      PasswordEncoder passwordEncoder) {
    this.otpRequestRepository = otpRequestRepository;
    this.appUserRepository = appUserRepository;
    this.organizationRepository = organizationRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.jwtService = jwtService;
    this.otpThrottleService = otpThrottleService;
    this.environment = environment;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void requestOtp(String phoneE164) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    Optional<OtpRequest> lastOtp = otpRequestRepository.findTopByPhoneE164OrderByCreatedAtDesc(phoneE164);

    UUID orgId = resolveOrgIdForOtp(phoneE164, lastOtp);

    OtpThrottleService.ThrottleState throttleState = otpThrottleService.check(phoneE164, now);
    String provider = "STATIC";

    OtpRequest request = lastOtp.orElseGet(() -> {
      OtpRequest created = new OtpRequest();
      created.setId(UUID.randomUUID());
      created.setOrgId(orgId);
      created.setPhoneE164(phoneE164);
      created.setAttemptCount(0);
      created.setConsumedAt(null);
      created.setCreatedAt(now);
      return created;
    });
    if (request.getOrgId() == null) {
      request.setOrgId(orgId);
    }
    request.setOtpHash(passwordEncoder.encode(STATIC_OTP));
    request.setExpiresAt(now.plus(OTP_EXPIRY));
    request.setConsumedAt(null);
    request.setAttemptCount(0);
    request.setLastSentAt(now);
    request.setSendCount(throttleState.nextSendCount());
    request.setProvider(provider);
    request.setProviderRef(null);

    log.info("STATIC OTP for {}: {}", phoneE164, STATIC_OTP);

    otpRequestRepository.save(request);
    otpThrottleService.recordSend(phoneE164, provider, now);
  }

  @Override
  public AuthResponse verifyOtp(String phoneE164, String otp) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    OtpRequest request = otpRequestRepository
        .findTopByPhoneE164OrderByCreatedAtDesc(phoneE164)
        .orElseThrow(() -> new BadRequestException("OTP not requested"));

    if (request.getConsumedAt() != null) {
      throw new BadRequestException("OTP_ALREADY_USED");
    }

    if (request.getExpiresAt().isBefore(now)) {
      throw new BadRequestException("OTP_EXPIRED");
    }

    Integer attemptCount = request.getAttemptCount() == null ? 0 : request.getAttemptCount();
    if (attemptCount >= 5) {
      throw new TooManyRequestsException("OTP_TOO_MANY_ATTEMPTS");
    }

    boolean matches;
    if (request.getOtpHash() != null) {
      matches = passwordEncoder.matches(otp, request.getOtpHash());
    } else {
      matches = STATIC_OTP.equals(otp);
    }
    if (!matches) {
      request.setAttemptCount(attemptCount + 1);
      otpRequestRepository.save(request);
      throw new BadRequestException("OTP_INVALID");
    }

    request.setConsumedAt(now);
    otpRequestRepository.save(request);

    AppUser user = appUserRepository.findTopByPhoneE164OrderByCreatedAtDesc(phoneE164).orElse(null);
    if (user == null) {
      Organization org = new Organization();
      org.setId(request.getOrgId());
      org.setName("Organization");
      org.setCreatedAt(now);
      org.setOnboardingCompleted(false);
      organizationRepository.save(org);
      createTrialSubscriptionIfMissing(org.getId(), now);

      AppUser created = new AppUser();
      created.setId(UUID.randomUUID());
      created.setOrgId(org.getId());
      created.setPhoneE164(phoneE164);
      created.setIsActive(true);
      created.setCreatedAt(now);
      created.setUpdatedAt(now);
      user = appUserRepository.save(created);
    }

    String token = jwtService.generateToken(user.getId(), user.getOrgId(), user.getPhoneE164());
    return new AuthResponse(token, user.getId(), user.getOrgId(), user.getPhoneE164());
  }

  private UUID resolveOrgIdForOtp(String phoneE164, Optional<OtpRequest> lastOtp) {
    AppUser existingUser = appUserRepository.findTopByPhoneE164OrderByCreatedAtDesc(phoneE164).orElse(null);
    if (existingUser != null) {
      return existingUser.getOrgId();
    }

    if (lastOtp.isPresent()) {
      return lastOtp.get().getOrgId();
    }

    Organization org = new Organization();
    org.setId(UUID.randomUUID());
    org.setName("Organization");
    org.setCreatedAt(OffsetDateTime.now());
    org.setOnboardingCompleted(false);
    organizationRepository.save(org);
    return org.getId();
  }

  private void createTrialSubscriptionIfMissing(UUID orgId, OffsetDateTime now) {
    if (subscriptionRepository.findByOrgId(orgId).isPresent()) {
      return;
    }
    Subscription subscription = new Subscription();
    subscription.setId(UUID.randomUUID());
    subscription.setOrgId(orgId);
    subscription.setPlanCode("GROWTH");
    subscription.setStatus("TRIAL");
    subscription.setTrialEndsAt(now.plusDays(14));
    subscription.setCurrentPeriodStart(null);
    subscription.setCurrentPeriodEnd(null);
    subscription.setCreatedAt(now);
    subscription.setUpdatedAt(now);
    subscriptionRepository.save(subscription);
  }

  private boolean isProd() {
    return Arrays.asList(environment.getActiveProfiles()).contains("prod");
  }
}
