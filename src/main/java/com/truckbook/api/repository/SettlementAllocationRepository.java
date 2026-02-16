package com.truckbook.api.repository;

import com.truckbook.api.entity.SettlementAllocation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementAllocationRepository extends JpaRepository<SettlementAllocation, UUID> {
  List<SettlementAllocation> findAllByOrgIdAndSettlementId(UUID orgId, UUID settlementId);

  @Query("select coalesce(sum(sa.allocatedAmount), 0) from SettlementAllocation sa "
      + "where sa.orgId = :orgId and sa.settlementId = :settlementId")
  BigDecimal sumAppliedByOrgIdAndSettlementId(
      @Param("orgId") UUID orgId,
      @Param("settlementId") UUID settlementId);

  @Query("select coalesce(sum(sa.allocatedAmount), 0) from SettlementAllocation sa "
      + "where sa.orgId = :orgId and sa.tripId = :tripId")
  BigDecimal sumAppliedByOrgIdAndTripId(
      @Param("orgId") UUID orgId,
      @Param("tripId") UUID tripId);

  boolean existsByOrgIdAndTripId(UUID orgId, UUID tripId);

  @Query("select sa.tripId as tripId, sum(sa.allocatedAmount) as totalApplied "
      + "from SettlementAllocation sa "
      + "where sa.orgId = :orgId and sa.tripId in :tripIds "
      + "group by sa.tripId")
  List<TripAllocationSum> sumAppliedByOrgIdAndTripIdIn(
      @Param("orgId") UUID orgId,
      @Param("tripIds") List<UUID> tripIds);

  @Query("select sa.settlementId as settlementId, sum(sa.allocatedAmount) as totalApplied "
      + "from SettlementAllocation sa "
      + "where sa.orgId = :orgId and sa.settlementId in :settlementIds "
      + "group by sa.settlementId")
  List<SettlementAllocationSum> sumAppliedByOrgIdAndSettlementIdIn(
      @Param("orgId") UUID orgId,
      @Param("settlementIds") List<UUID> settlementIds);

  @Query("select t.partyId as partyId, sum(sa.allocatedAmount) as totalPaid "
      + "from SettlementAllocation sa "
      + "join Trip t on t.id = sa.tripId and t.orgId = sa.orgId "
      + "where sa.orgId = :orgId and t.partyId is not null "
      + "group by t.partyId")
  List<PartyPaidSummary> sumPaidByParty(@Param("orgId") UUID orgId);

  @Query("select t.truckId as truckId, sum(sa.allocatedAmount) as totalPaid "
      + "from SettlementAllocation sa "
      + "join Trip t on t.id = sa.tripId and t.orgId = sa.orgId "
      + "where sa.orgId = :orgId and t.truckId is not null "
      + "group by t.truckId")
  List<TruckPaidSummary> sumPaidByTruck(@Param("orgId") UUID orgId);

  @Query("select t.truckId as truckId, sum(sa.allocatedAmount) as totalPaid "
      + "from SettlementAllocation sa "
      + "join Trip t on t.id = sa.tripId and t.orgId = sa.orgId "
      + "where sa.orgId = :orgId and t.partyId = :partyId and t.truckId is not null "
      + "group by t.truckId")
  List<PartyTruckPaidSummary> sumPaidByPartyAndTruck(
      @Param("orgId") UUID orgId,
      @Param("partyId") UUID partyId);

  @Query(value = "select coalesce(sum(sa.allocated_amount), 0) "
      + "from truckbook.settlement_allocations sa "
      + "join truckbook.settlements s on s.id = sa.settlement_id and s.org_id = sa.org_id "
      + "join truckbook.trips t on t.id = sa.trip_id and t.org_id = sa.org_id "
      + "where sa.org_id = :orgId "
      + "and s.settlement_date between :from and :to "
      + "and (:truckId is null or t.truck_id = :truckId) "
      + "and (:partyId is null or t.party_id = :partyId)",
      nativeQuery = true)
  BigDecimal sumAllocatedBySettlementDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query(value = "select coalesce(sum(sa.allocated_amount), 0) "
      + "from truckbook.settlement_allocations sa "
      + "join truckbook.trips t on t.id = sa.trip_id and t.org_id = sa.org_id "
      + "where sa.org_id = :orgId "
      + "and t.start_date between :from and :to "
      + "and (:truckId is null or t.truck_id = :truckId) "
      + "and (:partyId is null or t.party_id = :partyId)",
      nativeQuery = true)
  BigDecimal sumAllocatedByTripDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query(value = "select t.party_id as partyId, coalesce(sum(sa.allocated_amount), 0) as receivedAmount "
      + "from truckbook.settlement_allocations sa "
      + "join truckbook.settlements s on s.id = sa.settlement_id and s.org_id = sa.org_id "
      + "join truckbook.trips t on t.id = sa.trip_id and t.org_id = sa.org_id "
      + "where sa.org_id = :orgId "
      + "and s.settlement_date between :from and :to "
      + "and t.party_id is not null "
      + "and (:truckId is null or t.truck_id = :truckId) "
      + "and (:partyId is null or t.party_id = :partyId) "
      + "group by t.party_id",
      nativeQuery = true)
  List<PartyReceivedSummary> sumReceivedByPartyFromAllocations(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.partyId as partyId, coalesce(sum(sa.allocatedAmount), 0) as receivedAmount "
      + "from SettlementAllocation sa "
      + "join Trip t on t.id = sa.tripId and t.orgId = sa.orgId "
      + "where sa.orgId = :orgId "
      + "and t.startDate between :from and :to "
      + "and t.partyId is not null "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId) "
      + "group by t.partyId")
  List<PartyReceivedSummary> sumPaidByPartyForTripDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);
}
