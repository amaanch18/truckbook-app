package com.truckbook.api.repository;

import com.truckbook.api.entity.Trip;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface TripRepository extends JpaRepository<Trip, UUID>, JpaSpecificationExecutor<Trip> {
  List<Trip> findAllByOrgId(UUID orgId);
  long countByOrgId(UUID orgId);
  Optional<Trip> findByOrgIdAndId(UUID orgId, UUID id);

  List<Trip> findAllByOrgIdAndIdIn(UUID orgId, List<UUID> ids);
  Optional<Trip> findByOrgIdAndTripCode(UUID orgId, String tripCode);
  List<Trip> findByOrgIdAndTruckId(UUID orgId, UUID truckId);
  List<Trip> findAllByOrgIdAndPartyId(UUID orgId, UUID partyId);
  boolean existsByOrgIdAndTruckId(UUID orgId, UUID truckId);
  boolean existsByOrgIdAndPartyId(UUID orgId, UUID partyId);

  @Query("select t.partyId as partyId, p.name as partyName, sum(t.freightAmount) as totalFreight "
      + "from Trip t "
      + "join Party p on p.id = t.partyId and p.orgId = t.orgId "
      + "where t.orgId = :orgId and t.partyId is not null "
      + "group by t.partyId, p.name")
  List<PartyFreightSummary> sumFreightByParty(@Param("orgId") UUID orgId);

  @Query("select t.truckId as truckId, tr.truckNumber as truckNumber, sum(t.freightAmount) as totalFreight "
      + "from Trip t "
      + "join Truck tr on tr.id = t.truckId and tr.orgId = t.orgId "
      + "where t.orgId = :orgId and t.truckId is not null "
      + "group by t.truckId, tr.truckNumber")
  List<TruckFreightSummary> sumFreightByTruck(@Param("orgId") UUID orgId);

  @Query("select t.truckId as truckId, tr.truckNumber as truckNumber, sum(t.freightAmount) as totalFreight "
      + "from Trip t "
      + "join Truck tr on tr.id = t.truckId and tr.orgId = t.orgId "
      + "where t.orgId = :orgId and t.partyId = :partyId and t.truckId is not null "
      + "group by t.truckId, tr.truckNumber")
  List<PartyTruckFreightSummary> sumFreightByPartyAndTruck(
      @Param("orgId") UUID orgId,
      @Param("partyId") UUID partyId);

  @Query("select t.truckId as truckId, t.partyId as partyId, sum(t.freightAmount) as totalFreight "
      + "from Trip t "
      + "where t.orgId = :orgId and t.truckId is not null and t.partyId is not null "
      + "group by t.truckId, t.partyId")
  List<TruckPartyFreightSummary> sumFreightByTruckAndParty(@Param("orgId") UUID orgId);

  @Query("select coalesce(sum(t.freightAmount), 0) from Trip t "
      + "where t.orgId = :orgId and t.status = 'COMPLETED' "
      + "and t.startDate between :from and :to "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId)")
  BigDecimal sumRevenueCompleted(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.partyId as partyId, p.name as partyName, sum(t.freightAmount) as revenueEarned "
      + "from Trip t join Party p on p.id = t.partyId and p.orgId = t.orgId "
      + "where t.orgId = :orgId and t.status = 'COMPLETED' "
      + "and t.startDate between :from and :to "
      + "and t.partyId is not null "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId) "
      + "group by t.partyId, p.name")
  List<PartyRevenueSummary> sumRevenueByParty(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.truckId as truckId, tr.truckNumber as truckNumber, sum(t.freightAmount) as revenueEarned "
      + "from Trip t join Truck tr on tr.id = t.truckId and tr.orgId = t.orgId "
      + "where t.orgId = :orgId and t.status = 'COMPLETED' "
      + "and t.startDate between :from and :to "
      + "and t.truckId is not null "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId) "
      + "group by t.truckId, tr.truckNumber")
  List<TruckRevenueSummary> sumRevenueByTruck(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.truckId as truckId, count(t.id) as tripCount "
      + "from Trip t "
      + "where t.orgId = :orgId "
      + "and t.startDate between :from and :to "
      + "and t.truckId is not null "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId) "
      + "group by t.truckId")
  List<TruckTripCountSummary> countTripsByTruck(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select t.id as id, t.fromLocation as fromLocation, t.toLocation as toLocation, "
      + "t.status as status, t.freightAmount as freightAmount, t.startDate as startDate, "
      + "t.truckId as truckId, tr.truckNumber as truckNumber "
      + "from Trip t "
      + "left join Truck tr on tr.id = t.truckId and tr.orgId = t.orgId "
      + "where t.orgId = :orgId "
      + "order by t.startDate desc, t.createdAt desc")
  Page<RecentTripProjection> findRecentTripsByOrgId(
      @Param("orgId") UUID orgId,
      Pageable pageable);

  @Query(value = "select coalesce(sum(greatest(t.freight_amount - coalesce(sa.total_allocated, 0), 0)), 0) "
      + "from truckbook.trips t "
      + "left join ( "
      + "  select sa.trip_id, sum(sa.allocated_amount) as total_allocated "
      + "  from truckbook.settlement_allocations sa "
      + "  join truckbook.settlements s on s.id = sa.settlement_id and s.org_id = :orgId "
      + "  where sa.org_id = :orgId "
      + "  group by sa.trip_id "
      + ") sa on sa.trip_id = t.id "
      + "where t.org_id = :orgId",
      nativeQuery = true)
  BigDecimal sumPendingSettlementByOrgId(@Param("orgId") UUID orgId);

  @Query("select t.id as tripId, t.tripCode as tripCode, t.fromLocation as fromLocation, "
      + "t.toLocation as toLocation, t.truckId as truckId, tr.truckNumber as truckNumber, "
      + "t.freightAmount as freightAmount, t.status as status, t.startDate as startDate "
      + "from Trip t "
      + "left join Truck tr on tr.id = t.truckId and tr.orgId = t.orgId "
      + "where t.orgId = :orgId and t.startDate between :from and :to "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId) "
      + "order by t.startDate desc")
  List<TripReportProjection> findTripsForProfitBreakdown(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select count(t.id) from Trip t "
      + "where t.orgId = :orgId and t.status = 'COMPLETED' "
      + "and t.startDate between :from and :to "
      + "and (:truckId is null or t.truckId = :truckId) "
      + "and (:partyId is null or t.partyId = :partyId)")
  Long countCompletedTrips(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);
}
