package com.truckbook.api.repository;

import com.truckbook.api.entity.Settlement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {
  List<Settlement> findAllByOrgIdOrderBySettlementDateDesc(UUID orgId);
  Optional<Settlement> findByOrgIdAndId(UUID orgId, UUID id);
  Optional<Settlement> findByOrgIdAndSettlementCode(UUID orgId, String settlementCode);
  List<Settlement> findAllByOrgIdAndPartyIdOrderBySettlementDateAsc(UUID orgId, UUID partyId);

  @Query("select coalesce(sum(s.receivedAmount), 0) from Settlement s "
      + "where s.orgId = :orgId and s.settlementDate between :from and :to "
      + "and (:truckId is null or s.truckId = :truckId) "
      + "and (:partyId is null or s.partyId = :partyId)")
  BigDecimal sumReceivedAmount(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query("select s.partyId as partyId, coalesce(sum(s.receivedAmount), 0) as receivedAmount "
      + "from Settlement s "
      + "where s.orgId = :orgId and s.settlementDate between :from and :to "
      + "and s.partyId is not null "
      + "and (:truckId is null or s.truckId = :truckId) "
      + "and (:partyId is null or s.partyId = :partyId) "
      + "group by s.partyId")
  List<PartyReceivedSummary> sumReceivedByParty(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("truckId") UUID truckId,
      @Param("partyId") UUID partyId);

  @Query(value = "select coalesce(sum(greatest(s.received_amount - coalesce(a.allocated, 0), 0)), 0) "
      + "from truckbook.settlements s "
      + "left join ( "
      + "  select settlement_id, sum(allocated_amount) as allocated "
      + "  from truckbook.settlement_allocations "
      + "  group by settlement_id "
      + ") a on a.settlement_id = s.id "
      + "where s.org_id = :orgId and s.party_id = :partyId",
      nativeQuery = true)
  BigDecimal sumPartyCredit(
      @Param("orgId") UUID orgId,
      @Param("partyId") UUID partyId);

  @Query(value = "select s.party_id as partyId, "
      + "coalesce(sum(greatest(s.received_amount - coalesce(a.allocated, 0), 0)), 0) as creditAmount "
      + "from truckbook.settlements s "
      + "left join ( "
      + "  select settlement_id, sum(allocated_amount) as allocated "
      + "  from truckbook.settlement_allocations "
      + "  group by settlement_id "
      + ") a on a.settlement_id = s.id "
      + "where s.org_id = :orgId and s.party_id is not null "
      + "group by s.party_id",
      nativeQuery = true)
  List<PartyCreditSummary> sumPartyCreditByParty(@Param("orgId") UUID orgId);
}
