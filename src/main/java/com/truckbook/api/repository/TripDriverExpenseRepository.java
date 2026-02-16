package com.truckbook.api.repository;

import com.truckbook.api.entity.TripDriverExpense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripDriverExpenseRepository extends JpaRepository<TripDriverExpense, UUID> {
  List<TripDriverExpense> findAllByOrgIdAndTripIdOrderByEntryDateDesc(UUID orgId, UUID tripId);
  Optional<TripDriverExpense> findByOrgIdAndTripIdAndId(UUID orgId, UUID tripId, UUID id);

  @Query("select coalesce(sum(d.amount), 0) from TripDriverExpense d where d.orgId = :orgId and d.tripId = :tripId")
  BigDecimal sumAmountByOrgIdAndTripId(@Param("orgId") UUID orgId, @Param("tripId") UUID tripId);

  @Query("select d.tripId as tripId, sum(d.amount) as totalAmount "
      + "from TripDriverExpense d where d.orgId = :orgId and d.tripId in :tripIds group by d.tripId")
  List<TripAmountSum> sumAmountByOrgIdAndTripIdIn(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds);

  @Query("select d.tripId as tripId, sum(d.amount) as totalAmount "
      + "from TripDriverExpense d join Trip t on t.id = d.tripId and t.orgId = d.orgId "
      + "where d.orgId = :orgId and d.tripId in :tripIds "
      + "and t.startDate between :from and :to "
      + "group by d.tripId")
  List<TripAmountSum> sumAmountByOrgIdAndTripIdInDateRange(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select coalesce(sum(d.amount), 0) from TripDriverExpense d "
      + "join Trip t on t.id = d.tripId and t.orgId = d.orgId "
      + "where d.orgId = :orgId and t.startDate between :from and :to "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId)")
  BigDecimal sumAmountByTripDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.truckId as truckId, sum(d.amount) as totalAmount "
      + "from TripDriverExpense d join Trip t on t.id = d.tripId and t.orgId = d.orgId "
      + "where d.orgId = :orgId and t.startDate between :from and :to "
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
