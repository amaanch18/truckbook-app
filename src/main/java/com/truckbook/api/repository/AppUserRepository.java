package com.truckbook.api.repository;

import com.truckbook.api.entity.AppUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
  Optional<AppUser> findByOrgIdAndPhoneE164(UUID orgId, String phoneE164);

  Optional<AppUser> findTopByPhoneE164OrderByCreatedAtDesc(String phoneE164);
}
