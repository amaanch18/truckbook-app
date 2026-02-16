package com.truckbook.api.repository;

import com.truckbook.api.entity.OtpRequest;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface OtpRequestRepository extends JpaRepository<OtpRequest, UUID> {
  Optional<OtpRequest> findTopByPhoneE164OrderByCreatedAtDesc(String phoneE164);

  Optional<OtpRequest> findTopByPhoneE164AndConsumedAtIsNullOrderByCreatedAtDesc(String phoneE164);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<OtpRequest> findTopByPhoneE164OrderByLastSentAtDesc(String phoneE164);
}
