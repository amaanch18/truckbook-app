package com.truckbook.api.repository;

import com.truckbook.api.entity.TripFuelLog;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripFuelLogRepository extends JpaRepository<TripFuelLog, UUID> {
  List<TripFuelLog> findAllByOrgIdAndTripIdOrderByEntryDateDesc(UUID orgId, UUID tripId);
  Optional<TripFuelLog> findByOrgIdAndTripIdAndId(UUID orgId, UUID tripId, UUID id);

  @Query("select coalesce(sum(f.amount), 0) from TripFuelLog f where f.orgId = :orgId and f.tripId = :tripId")
  BigDecimal sumAmountByOrgIdAndTripId(@Param("orgId") UUID orgId, @Param("tripId") UUID tripId);

  @Query("select f.tripId as tripId, sum(f.amount) as totalAmount "
      + "from TripFuelLog f where f.orgId = :orgId and f.tripId in :tripIds group by f.tripId")
  List<TripAmountSum> sumAmountByOrgIdAndTripIdIn(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds);

  @Query("select f.tripId as tripId, sum(f.amount) as totalAmount "
      + "from TripFuelLog f join Trip t on t.id = f.tripId and t.orgId = f.orgId "
      + "where f.orgId = :orgId and f.tripId in :tripIds "
      + "and t.startDate between :from and :to "
      + "group by f.tripId")
  List<TripAmountSum> sumAmountByOrgIdAndTripIdInDateRange(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select coalesce(sum(f.amount), 0) from TripFuelLog f "
      + "join Trip t on t.id = f.tripId and t.orgId = f.orgId "
      + "where f.orgId = :orgId and t.startDate between :from and :to "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId)")
  BigDecimal sumAmountByTripDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.truckId as truckId, sum(f.amount) as totalAmount "
      + "from TripFuelLog f join Trip t on t.id = f.tripId and t.orgId = f.orgId "
      + "where f.orgId = :orgId and t.startDate between :from and :to "
      + "and t.truckId is not null "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId) "
      + "group by t.truckId")
  List<TruckExpenseSummary> sumAmountByTruck(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);
}
