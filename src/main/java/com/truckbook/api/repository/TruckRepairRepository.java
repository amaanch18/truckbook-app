package com.truckbook.api.repository;

import com.truckbook.api.entity.TruckRepair;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TruckRepairRepository extends JpaRepository<TruckRepair, UUID> {
  List<TruckRepair> findAllByOrgIdAndTruckIdOrderByEntryDateDesc(UUID orgId, UUID truckId);
  List<TruckRepair> findAllByOrgIdAndTruckIdAndEntryDateBetweenOrderByEntryDateDesc(
      UUID orgId,
      UUID truckId,
      LocalDate from,
      LocalDate to);
  Optional<TruckRepair> findByOrgIdAndTruckIdAndId(UUID orgId, UUID truckId, UUID id);

  @Query("select coalesce(sum(r.amount),0) from TruckRepair r where r.orgId=:orgId and r.entryDate between :from and :to")
  BigDecimal sumByOrgIdAndDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select coalesce(sum(r.amount),0) from TruckRepair r where r.orgId=:orgId and r.truckId=:truckId and r.entryDate between :from and :to")
  BigDecimal sumByOrgIdTruckIdAndDateRange(
      @Param("orgId") UUID orgId,
      @Param("truckId") UUID truckId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select r.truckId as truckId, sum(r.amount) as totalAmount from TruckRepair r "
      + "where r.orgId=:orgId and r.entryDate between :from and :to group by r.truckId")
  List<TruckExpenseSummary> sumByTruck(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);
}
