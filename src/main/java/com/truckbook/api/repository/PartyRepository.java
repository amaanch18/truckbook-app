package com.truckbook.api.repository;

import com.truckbook.api.entity.Party;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, UUID> {
  List<Party> findAllByOrgId(UUID orgId);
  List<Party> findAllByOrgIdOrderByNameAsc(UUID orgId);
  Optional<Party> findByOrgIdAndId(UUID orgId, UUID id);
  Optional<Party> findByOrgIdAndNameIgnoreCase(UUID orgId, String name);
  List<Party> findByOrgIdAndNameContainingIgnoreCaseOrderByNameAsc(UUID orgId, String q);
}
