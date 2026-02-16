package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.AuthResponse;
import com.truckbook.api.entity.AppUser;
import com.truckbook.api.entity.Organization;
import com.truckbook.api.entity.OtpRequest;
import com.truckbook.api.entity.Subscription;
import com.truckbook.api.exception.BadGatewayException;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.repository.AppUserRepository;
import com.truckbook.api.repository.OrganizationRepository;
import com.truckbook.api.repository.OtpRequestRepository;
import com.truckbook.api.repository.SubscriptionRepository;
import com.truckbook.api.security.JwtService;
import com.truckbook.api.service.Msg91OtpService;
import com.truckbook.api.service.AuthService;
import com.truckbook.api.service.OtpThrottleService;
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

@Service
public class AuthServiceImpl implements AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
  private static final Duration OTP_EXPIRY = Duration.ofMinutes(5);

  private final OtpRequestRepository otpRequestRepository;
  private final AppUserRepository appUserRepository;
  private final OrganizationRepository organizationRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final JwtService jwtService;
  private final Msg91OtpService msg91OtpService;
  private final OtpThrottleService otpThrottleService;
  private final Environment environment;

  public AuthServiceImpl(
      OtpRequestRepository otpRequestRepository,
      AppUserRepository appUserRepository,
      OrganizationRepository organizationRepository,
      SubscriptionRepository subscriptionRepository,
      JwtService jwtService,
      Msg91OtpService msg91OtpService,
      OtpThrottleService otpThrottleService,
      Environment environment) {
    this.otpRequestRepository = otpRequestRepository;
    this.appUserRepository = appUserRepository;
    this.organizationRepository = organizationRepository;
    this.subscriptionRepository = subscriptionRepository;
    this.jwtService = jwtService;
    this.msg91OtpService = msg91OtpService;
    this.otpThrottleService = otpThrottleService;
    this.environment = environment;
  }

  @Override
  @Transactional
  public void requestOtp(String phoneE164) {
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    Optional<OtpRequest> lastOtp = otpRequestRepository.findTopByPhoneE164OrderByCreatedAtDesc(phoneE164);

    UUID orgId = resolveOrgIdForOtp(phoneE164, lastOtp);

    OtpThrottleService.ThrottleState throttleState = otpThrottleService.check(phoneE164, now);
    String provider = isProd() ? "MSG91" : "DEV";

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
    request.setOtpHash(null);
    request.setExpiresAt(now.plus(OTP_EXPIRY));
    request.setConsumedAt(null);
    request.setLastSentAt(now);
    request.setSendCount(throttleState.nextSendCount());
    request.setProvider(provider);
    request.setProviderRef(null);

    try {
      if (isProd()) {
        msg91OtpService.sendOtp(phoneE164);
      } else {
        log.info("DEV OTP for {}: 123456", phoneE164);
      }
    } catch (Exception ex) {
      throw new BadGatewayException("OTP_PROVIDER_FAILED");
    }

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
      throw new BadRequestException("OTP already used");
    }

    if (request.getExpiresAt().isBefore(now)) {
      throw new BadRequestException("OTP expired");
    }

    if (isProd()) {
      msg91OtpService.verifyOtp(phoneE164, otp);
    } else {
      if (!"123456".equals(otp)) {
        throw new BadRequestException("Invalid OTP");
      }
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
