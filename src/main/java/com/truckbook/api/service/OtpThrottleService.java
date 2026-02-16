package com.truckbook.api.service;

import com.truckbook.api.entity.OtpRequest;
import com.truckbook.api.entity.OtpThrottle;
import com.truckbook.api.exception.TooManyRequestsException;
import com.truckbook.api.repository.OtpRequestRepository;
import com.truckbook.api.repository.OtpThrottleRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OtpThrottleService {
  private static final Duration MIN_INTERVAL = Duration.ofSeconds(30);
  private static final Duration HOURLY_WINDOW = Duration.ofHours(1);
  private static final int MAX_PER_HOUR = 5;

  private final OtpRequestRepository otpRequestRepository;
  private final OtpThrottleRepository otpThrottleRepository;

  public OtpThrottleService(
      OtpRequestRepository otpRequestRepository,
      OtpThrottleRepository otpThrottleRepository) {
    this.otpRequestRepository = otpRequestRepository;
    this.otpThrottleRepository = otpThrottleRepository;
  }

  public ThrottleState check(String phoneE164, OffsetDateTime now) {
    Optional<OtpRequest> last = otpRequestRepository.findTopByPhoneE164OrderByLastSentAtDesc(phoneE164);
    if (last.isPresent()) {
      OffsetDateTime lastSentAt = last.get().getLastSentAt();
      if (lastSentAt != null && lastSentAt.isAfter(now.minus(MIN_INTERVAL))) {
        throw new TooManyRequestsException("OTP_RATE_LIMIT");
      }
    }

    long sentLastHour = otpThrottleRepository.countByPhoneE164AndSentAtAfter(phoneE164, now.minus(HOURLY_WINDOW));
    if (sentLastHour >= MAX_PER_HOUR) {
      throw new TooManyRequestsException("OTP_HOURLY_LIMIT");
    }

    int nextCount = last.map(OtpRequest::getSendCount).orElse(0) + 1;
    return new ThrottleState(nextCount);
  }

  public void recordSend(String phoneE164, String provider, OffsetDateTime now) {
    OtpThrottle throttle = new OtpThrottle();
    throttle.setId(UUID.randomUUID());
    throttle.setPhoneE164(phoneE164);
    throttle.setSentAt(now);
    throttle.setProvider(provider);
    otpThrottleRepository.save(throttle);
  }

  public record ThrottleState(int nextSendCount) {}
}
