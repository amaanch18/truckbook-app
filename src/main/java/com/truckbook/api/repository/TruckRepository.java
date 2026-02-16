package com.truckbook.api.repository;

import com.truckbook.api.entity.Truck;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<Truck, UUID> {
  List<Truck> findAllByOrgId(UUID orgId);
  long countByOrgId(UUID orgId);
  Optional<Truck> findByOrgIdAndId(UUID orgId, UUID id);
  Optional<Truck> findByOrgIdAndTruckNumber(UUID orgId, String truckNumber);
  List<Truck> findAllByOrgIdAndIdIn(UUID orgId, List<UUID> ids);
}
