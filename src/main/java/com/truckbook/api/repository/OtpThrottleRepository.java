package com.truckbook.api.repository;

import com.truckbook.api.entity.OtpThrottle;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpThrottleRepository extends JpaRepository<OtpThrottle, UUID> {
  long countByPhoneE164AndSentAtAfter(String phoneE164, OffsetDateTime since);
}
