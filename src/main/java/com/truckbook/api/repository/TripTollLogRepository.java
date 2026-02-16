package com.truckbook.api.repository;

import com.truckbook.api.entity.TripTollLog;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripTollLogRepository extends JpaRepository<TripTollLog, UUID> {
  List<TripTollLog> findAllByOrgIdAndTripIdOrderByEntryDateDesc(UUID orgId, UUID tripId);
  Optional<TripTollLog> findByOrgIdAndTripIdAndId(UUID orgId, UUID tripId, UUID id);

  @Query("select coalesce(sum(t.amount), 0) from TripTollLog t where t.orgId = :orgId and t.tripId = :tripId")
  BigDecimal sumAmountByOrgIdAndTripId(@Param("orgId") UUID orgId, @Param("tripId") UUID tripId);

  @Query("select t.tripId as tripId, sum(t.amount) as totalAmount "
      + "from TripTollLog t where t.orgId = :orgId and t.tripId in :tripIds group by t.tripId")
  List<TripAmountSum> sumAmountByOrgIdAndTripIdIn(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds);

  @Query("select t.tripId as tripId, sum(t.amount) as totalAmount "
      + "from TripTollLog t join Trip tr on tr.id = t.tripId and tr.orgId = t.orgId "
      + "where t.orgId = :orgId and t.tripId in :tripIds "
      + "and tr.startDate between :from and :to "
      + "group by t.tripId")
  List<TripAmountSum> sumAmountByOrgIdAndTripIdInDateRange(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select coalesce(sum(tl.amount), 0) from TripTollLog tl "
      + "join Trip t on t.id = tl.tripId and t.orgId = tl.orgId "
      + "where tl.orgId = :orgId and t.startDate between :from and :to "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId)")
  BigDecimal sumAmountByTripDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.truckId as truckId, sum(tl.amount) as totalAmount "
      + "from TripTollLog tl join Trip t on t.id = tl.tripId and t.orgId = tl.orgId "
      + "where tl.orgId = :orgId and t.startDate between :from and :to "
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
