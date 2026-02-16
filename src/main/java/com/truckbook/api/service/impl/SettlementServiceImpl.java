package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.OutstandingPartyRow;
import com.truckbook.api.controller.dto.OutstandingPartyTruckRow;
import com.truckbook.api.controller.dto.OutstandingTripRow;
import com.truckbook.api.controller.dto.OutstandingTruckRow;
import com.truckbook.api.controller.dto.SettlementAllocateRequest;
import com.truckbook.api.controller.dto.SettlementAllocationItemRequest;
import com.truckbook.api.controller.dto.SettlementAllocationResponse;
import com.truckbook.api.controller.dto.SettlementCreateRequest;
import com.truckbook.api.controller.dto.SettlementDetailResponse;
import com.truckbook.api.controller.dto.SettlementResponse;
import com.truckbook.api.entity.Party;
import com.truckbook.api.entity.Settlement;
import com.truckbook.api.entity.SettlementAllocation;
import com.truckbook.api.entity.Trip;
import com.truckbook.api.entity.Truck;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.exception.ConflictException;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.PartyFreightSummary;
import com.truckbook.api.repository.PartyPaidSummary;
import com.truckbook.api.repository.PartyRepository;
import com.truckbook.api.repository.PartyTruckFreightSummary;
import com.truckbook.api.repository.PartyTruckPaidSummary;
import com.truckbook.api.repository.SettlementAllocationRepository;
import com.truckbook.api.repository.SettlementAllocationSum;
import com.truckbook.api.repository.SettlementRepository;
import com.truckbook.api.repository.TripAllocationSum;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TruckFreightSummary;
import com.truckbook.api.repository.TruckPaidSummary;
import com.truckbook.api.repository.TruckPartyFreightSummary;
import com.truckbook.api.repository.TruckRepository;
import com.truckbook.api.service.SettlementService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettlementServiceImpl implements SettlementService {
  private static final Set<String> ALLOWED_MODES =
      Set.of("CASH", "UPI", "BANK", "OTHER");

  private final SettlementRepository settlementRepository;
  private final SettlementAllocationRepository settlementAllocationRepository;
  private final PartyRepository partyRepository;
  private final TruckRepository truckRepository;
  private final TripRepository tripRepository;

  public SettlementServiceImpl(
      SettlementRepository settlementRepository,
      SettlementAllocationRepository settlementAllocationRepository,
      PartyRepository partyRepository,
      TruckRepository truckRepository,
      TripRepository tripRepository) {
    this.settlementRepository = settlementRepository;
    this.settlementAllocationRepository = settlementAllocationRepository;
    this.partyRepository = partyRepository;
    this.truckRepository = truckRepository;
    this.tripRepository = tripRepository;
  }

  @Override
  public SettlementResponse create(UUID orgId, SettlementCreateRequest request) {
    String mode = normalizeMode(request.getPaymentMode());
    Party party = partyRepository.findByOrgIdAndId(orgId, request.getPartyId())
        .orElseThrow(() -> new NotFoundException("Party not found"));

    Truck truck = null;
    if (request.getTruckId() != null) {
      truck = truckRepository.findByOrgIdAndId(orgId, request.getTruckId())
          .orElseThrow(() -> new NotFoundException("Truck not found"));
    }

    Settlement settlement = new Settlement();
    settlement.setId(UUID.randomUUID());
    settlement.setOrgId(orgId);
    settlement.setSettlementCode(generateSettlementCode(orgId));
    settlement.setPartyId(party.getId());
    settlement.setTruckId(truck == null ? null : truck.getId());
    settlement.setSettlementDate(request.getSettlementDate());
    settlement.setReceivedAmount(request.getReceivedAmount());
    settlement.setUnallocatedAmount(request.getReceivedAmount());
    settlement.setMode(mode);
    settlement.setReference(request.getReference());
    settlement.setNotes(request.getNotes());
    settlement.setCreatedAt(OffsetDateTime.now());

    Settlement saved = settlementRepository.save(settlement);
    party.setCreditAmount(normalizeAmount(party.getCreditAmount())
        .add(normalizeAmount(saved.getUnallocatedAmount())));
    partyRepository.save(party);
    return toResponse(saved, BigDecimal.ZERO);
  }

  @Override
  public List<SettlementResponse> list(UUID orgId) {
    List<Settlement> settlements = settlementRepository.findAllByOrgIdOrderBySettlementDateDesc(orgId);
    if (settlements.isEmpty()) {
      return List.of();
    }

    List<UUID> settlementIds = settlements.stream().map(Settlement::getId).toList();
    Map<UUID, BigDecimal> allocatedBySettlement = settlementAllocationRepository
        .sumAppliedByOrgIdAndSettlementIdIn(orgId, settlementIds).stream()
        .collect(Collectors.toMap(
            SettlementAllocationSum::getSettlementId,
            sum -> normalizeAmount(sum.getTotalApplied())));

    return settlements.stream()
        .map(settlement -> toResponse(settlement, allocatedBySettlement.get(settlement.getId())))
        .toList();
  }

  @Override
  public SettlementDetailResponse get(UUID orgId, UUID id) {
    Settlement settlement = settlementRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Settlement not found"));

    List<SettlementAllocation> allocations =
        settlementAllocationRepository.findAllByOrgIdAndSettlementId(orgId, settlement.getId());
    List<UUID> tripIds = allocations.stream()
        .map(SettlementAllocation::getTripId)
        .toList();
    Map<UUID, BigDecimal> paidByTrip = tripIds.isEmpty()
        ? Map.of()
        : settlementAllocationRepository.sumAppliedByOrgIdAndTripIdIn(orgId, tripIds).stream()
            .collect(Collectors.toMap(
                TripAllocationSum::getTripId,
                sum -> normalizeAmount(sum.getTotalApplied())));
    Map<UUID, BigDecimal> freightByTrip = tripIds.isEmpty()
        ? Map.of()
        : tripRepository.findAllByOrgIdAndIdIn(orgId, tripIds).stream()
            .collect(Collectors.toMap(
                Trip::getId,
                trip -> normalizeAmount(trip.getFreightAmount())));
    SettlementResponse settlementResponse =
        toResponse(settlement, sumAllocations(allocations));
    List<SettlementAllocationResponse> allocationResponses = allocations.stream()
        .map(allocation -> {
          BigDecimal paid = paidByTrip.getOrDefault(allocation.getTripId(), BigDecimal.ZERO);
          BigDecimal freight = freightByTrip.getOrDefault(allocation.getTripId(), BigDecimal.ZERO);
          BigDecimal pending = freight.subtract(paid);
          if (pending.signum() < 0) {
            pending = BigDecimal.ZERO;
          }
          return toAllocationResponse(allocation, pending);
        })
        .toList();

    return new SettlementDetailResponse(settlementResponse, allocationResponses);
  }

  @Override
  @Transactional
  public SettlementDetailResponse allocate(UUID orgId, UUID settlementId, SettlementAllocateRequest request) {
    if (request.getAllocations() == null || request.getAllocations().isEmpty()) {
      throw new BadRequestException("At least one allocation is required");
    }

    Settlement settlement = settlementRepository.findByOrgIdAndId(orgId, settlementId)
        .orElseThrow(() -> new NotFoundException("Settlement not found"));
    Party party = partyRepository.findByOrgIdAndId(orgId, settlement.getPartyId())
        .orElseThrow(() -> new NotFoundException("Party not found"));

    List<SettlementAllocation> existingAllocations =
        settlementAllocationRepository.findAllByOrgIdAndSettlementId(orgId, settlementId);
    Set<UUID> existingTripIds = existingAllocations.stream()
        .map(SettlementAllocation::getTripId)
        .collect(Collectors.toSet());

    List<UUID> tripIds = request.getAllocations().stream()
        .map(SettlementAllocationItemRequest::getTripId)
        .toList();

    Map<UUID, BigDecimal> paidByTrip = settlementAllocationRepository
        .sumAppliedByOrgIdAndTripIdIn(orgId, tripIds).stream()
        .collect(Collectors.toMap(
            TripAllocationSum::getTripId,
            sum -> normalizeAmount(sum.getTotalApplied())));

    BigDecimal newTotal = request.getAllocations().stream()
        .map(SettlementAllocationItemRequest::getAmountApplied)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal partyCredit = normalizeAmount(party.getCreditAmount());
    BigDecimal settlementUnallocated = normalizeAmount(settlement.getUnallocatedAmount());
    BigDecimal totalAllocatable = partyCredit;
    if (newTotal.compareTo(totalAllocatable) > 0) {
      throw new BadRequestException("Allocations exceed available amount");
    }

    List<SettlementAllocation> toCreate = new java.util.ArrayList<>();
    OffsetDateTime now = OffsetDateTime.now();

    for (SettlementAllocationItemRequest item : request.getAllocations()) {
      if (existingTripIds.contains(item.getTripId())) {
        throw new ConflictException("Trip already allocated in this settlement");
      }

      Trip trip = tripRepository.findByOrgIdAndId(orgId, item.getTripId())
          .orElseThrow(() -> new NotFoundException("Trip not found"));

      if (trip.getPartyId() == null) {
        throw new ConflictException("Trip has no party assigned");
      }
      if (!trip.getPartyId().equals(settlement.getPartyId())) {
        throw new ConflictException("Trip party does not match settlement party");
      }
      if (settlement.getTruckId() != null) {
        if (trip.getTruckId() == null || !settlement.getTruckId().equals(trip.getTruckId())) {
          throw new ConflictException("Trip truck does not match settlement truck");
        }
      }

      BigDecimal alreadyPaid = normalizeAmount(paidByTrip.get(trip.getId()));
      BigDecimal freightAmount = normalizeAmount(trip.getFreightAmount());
      BigDecimal remainingForTrip = freightAmount.subtract(alreadyPaid);
      if (remainingForTrip.signum() < 0) {
        remainingForTrip = BigDecimal.ZERO;
      }
      if (item.getAmountApplied().compareTo(remainingForTrip) > 0) {
        throw new ConflictException("Allocation exceeds trip remaining amount");
      }

      SettlementAllocation allocation = new SettlementAllocation();
      allocation.setId(UUID.randomUUID());
      allocation.setOrgId(orgId);
      allocation.setSettlementId(settlementId);
      allocation.setTripId(trip.getId());
      allocation.setAllocatedAmount(item.getAmountApplied());
      allocation.setCreatedAt(now);
      toCreate.add(allocation);
    }

    settlementAllocationRepository.saveAll(toCreate);

    BigDecimal otherCredit = partyCredit.subtract(settlementUnallocated);
    if (otherCredit.signum() < 0) {
      otherCredit = BigDecimal.ZERO;
    }
    BigDecimal creditUsed = newTotal.min(otherCredit);
    BigDecimal remaining = newTotal.subtract(creditUsed);
    BigDecimal fromSettlement = remaining.min(settlementUnallocated);

    party.setCreditAmount(partyCredit.subtract(newTotal));

    BigDecimal remainingUnallocated = settlementUnallocated.subtract(fromSettlement);
    if (remainingUnallocated.signum() < 0) {
      remainingUnallocated = BigDecimal.ZERO;
    }
    settlement.setUnallocatedAmount(remainingUnallocated);
    settlementRepository.saveAndFlush(settlement);
    partyRepository.save(party);

    return get(orgId, settlementId);
  }

  @Override
  public List<OutstandingPartyRow> outstandingByParty(UUID orgId) {
    List<PartyFreightSummary> freightSummaries = tripRepository.sumFreightByParty(orgId);
    List<PartyPaidSummary> paidSummaries = settlementAllocationRepository.sumPaidByParty(orgId);
    Map<UUID, BigDecimal> creditByParty = partyRepository.findAllByOrgId(orgId).stream()
        .collect(Collectors.toMap(
            Party::getId,
            party -> normalizeAmount(party.getCreditAmount())));

    Map<UUID, BigDecimal> paidByParty = new HashMap<>();
    for (PartyPaidSummary summary : paidSummaries) {
      paidByParty.put(summary.getPartyId(), normalizeAmount(summary.getTotalPaid()));
    }

    List<OutstandingPartyRow> rows = new java.util.ArrayList<>();
    for (PartyFreightSummary summary : freightSummaries) {
      BigDecimal totalFreight = normalizeAmount(summary.getTotalFreight());
      BigDecimal totalPaid = paidByParty.getOrDefault(summary.getPartyId(), BigDecimal.ZERO);
      BigDecimal outstanding = totalFreight.subtract(totalPaid);
      if (outstanding.signum() <= 0) {
        continue;
      }
      BigDecimal partyCredit = creditByParty.getOrDefault(summary.getPartyId(), BigDecimal.ZERO);
      BigDecimal netOutstanding = outstanding.subtract(partyCredit);
      OutstandingPartyRow row = new OutstandingPartyRow();
      row.setPartyId(summary.getPartyId());
      row.setPartyName(summary.getPartyName());
      row.setTotalFreight(totalFreight);
      row.setTotalPaid(totalPaid);
      row.setTotalOutstanding(outstanding);
      row.setPartyCredit(partyCredit);
      row.setNetOutstanding(netOutstanding);
      rows.add(row);
    }
    return rows;
  }

  @Override
  public List<OutstandingTruckRow> outstandingByTruck(UUID orgId) {
    List<TruckFreightSummary> freightSummaries = tripRepository.sumFreightByTruck(orgId);
    List<TruckPaidSummary> paidSummaries = settlementAllocationRepository.sumPaidByTruck(orgId);
    List<TruckPartyFreightSummary> truckPartySummaries = tripRepository.sumFreightByTruckAndParty(orgId);

    Map<UUID, BigDecimal> paidByTruck = new HashMap<>();
    for (TruckPaidSummary summary : paidSummaries) {
      paidByTruck.put(summary.getTruckId(), normalizeAmount(summary.getTotalPaid()));
    }
    Map<UUID, BigDecimal> creditByParty = partyRepository.findAllByOrgId(orgId).stream()
        .collect(Collectors.toMap(
            Party::getId,
            party -> normalizeAmount(party.getCreditAmount())));
    Map<UUID, java.util.Set<UUID>> partiesByTruck = new HashMap<>();
    for (TruckPartyFreightSummary summary : truckPartySummaries) {
      partiesByTruck.computeIfAbsent(summary.getTruckId(), key -> new java.util.HashSet<>())
          .add(summary.getPartyId());
    }

    List<OutstandingTruckRow> rows = new java.util.ArrayList<>();
    for (TruckFreightSummary summary : freightSummaries) {
      BigDecimal totalFreight = normalizeAmount(summary.getTotalFreight());
      BigDecimal totalPaid = paidByTruck.getOrDefault(summary.getTruckId(), BigDecimal.ZERO);
      BigDecimal outstanding = totalFreight.subtract(totalPaid);
      if (outstanding.signum() <= 0) {
        continue;
      }
      BigDecimal partyCredit = BigDecimal.ZERO;
      java.util.Set<UUID> partyIds = partiesByTruck.getOrDefault(summary.getTruckId(), java.util.Set.of());
      for (UUID partyId : partyIds) {
        partyCredit = partyCredit.add(creditByParty.getOrDefault(partyId, BigDecimal.ZERO));
      }
      BigDecimal netOutstanding = outstanding.subtract(partyCredit);
      OutstandingTruckRow row = new OutstandingTruckRow();
      row.setTruckId(summary.getTruckId());
      row.setTruckNumber(summary.getTruckNumber());
      row.setTotalFreight(totalFreight);
      row.setTotalPaid(totalPaid);
      row.setTotalOutstanding(outstanding);
      row.setPartyCredit(partyCredit);
      row.setNetOutstanding(netOutstanding);
      rows.add(row);
    }
    return rows;
  }

  @Override
  public List<OutstandingTripRow> outstandingTripsByParty(UUID orgId, UUID partyId) {
    partyRepository.findByOrgIdAndId(orgId, partyId)
        .orElseThrow(() -> new NotFoundException("Party not found"));
    List<Trip> trips = tripRepository.findAllByOrgIdAndPartyId(orgId, partyId);
    return toOutstandingTripRows(orgId, trips);
  }

  @Override
  public List<OutstandingTripRow> outstandingTripsByTruck(UUID orgId, UUID truckId) {
    truckRepository.findByOrgIdAndId(orgId, truckId)
        .orElseThrow(() -> new NotFoundException("Truck not found"));
    List<Trip> trips = tripRepository.findByOrgIdAndTruckId(orgId, truckId);
    return toOutstandingTripRows(orgId, trips);
  }

  @Override
  public List<OutstandingPartyTruckRow> outstandingTrucksByParty(UUID orgId, UUID partyId) {
    partyRepository.findByOrgIdAndId(orgId, partyId)
        .orElseThrow(() -> new NotFoundException("Party not found"));

    List<PartyTruckFreightSummary> freightSummaries =
        tripRepository.sumFreightByPartyAndTruck(orgId, partyId);
    List<PartyTruckPaidSummary> paidSummaries =
        settlementAllocationRepository.sumPaidByPartyAndTruck(orgId, partyId);

    Map<UUID, BigDecimal> paidByTruck = new HashMap<>();
    for (PartyTruckPaidSummary summary : paidSummaries) {
      paidByTruck.put(summary.getTruckId(), normalizeAmount(summary.getTotalPaid()));
    }

    List<OutstandingPartyTruckRow> rows = new java.util.ArrayList<>();
    for (PartyTruckFreightSummary summary : freightSummaries) {
      BigDecimal totalFreight = normalizeAmount(summary.getTotalFreight());
      BigDecimal totalPaid = paidByTruck.getOrDefault(summary.getTruckId(), BigDecimal.ZERO);
      BigDecimal outstanding = totalFreight.subtract(totalPaid);
      if (outstanding.signum() <= 0) {
        continue;
      }
      OutstandingPartyTruckRow row = new OutstandingPartyTruckRow();
      row.setTruckId(summary.getTruckId());
      row.setTruckNumber(summary.getTruckNumber());
      row.setTotalFreight(totalFreight);
      row.setTotalPaid(totalPaid);
      row.setTotalOutstanding(outstanding);
      rows.add(row);
    }
    return rows;
  }

  private List<OutstandingTripRow> toOutstandingTripRows(UUID orgId, List<Trip> trips) {
    if (trips.isEmpty()) {
      return List.of();
    }
    List<UUID> tripIds = trips.stream().map(Trip::getId).toList();
    Map<UUID, BigDecimal> paidByTrip = settlementAllocationRepository
        .sumAppliedByOrgIdAndTripIdIn(orgId, tripIds).stream()
        .collect(Collectors.toMap(
            TripAllocationSum::getTripId,
            sum -> normalizeAmount(sum.getTotalApplied())));

    List<OutstandingTripRow> rows = new java.util.ArrayList<>();
    for (Trip trip : trips) {
      BigDecimal paid = paidByTrip.getOrDefault(trip.getId(), BigDecimal.ZERO);
      BigDecimal freight = normalizeAmount(trip.getFreightAmount());
      BigDecimal outstanding = freight.subtract(paid);
      if (outstanding.signum() < 0) {
        outstanding = BigDecimal.ZERO;
      }
      OutstandingTripRow row = new OutstandingTripRow();
      row.setTripId(trip.getId());
      row.setTripCode(trip.getTripCode());
      row.setFromLocation(trip.getFromLocation());
      row.setToLocation(trip.getToLocation());
      row.setStartDate(trip.getStartDate());
      row.setFreightAmount(freight);
      row.setPaidAmount(paid);
      row.setOutstandingAmount(outstanding);
      row.setBillingStatus(resolveBillingStatus(freight, paid));
      rows.add(row);
    }
    return rows;
  }

  private SettlementResponse toResponse(Settlement settlement, BigDecimal allocatedAmountRaw) {
    BigDecimal allocatedAmount = normalizeAmount(allocatedAmountRaw);
    BigDecimal receivedAmount = normalizeAmount(settlement.getReceivedAmount());
    BigDecimal unallocatedAmount = normalizeAmount(settlement.getUnallocatedAmount());

    SettlementResponse response = new SettlementResponse();
    response.setId(settlement.getId());
    response.setOrgId(settlement.getOrgId());
    response.setSettlementCode(settlement.getSettlementCode());
    response.setPartyId(settlement.getPartyId());
    response.setTruckId(settlement.getTruckId());
    response.setSettlementDate(settlement.getSettlementDate());
    response.setReceivedAmount(receivedAmount);
    response.setPaymentMode(settlement.getMode());
    response.setReference(settlement.getReference());
    response.setNotes(settlement.getNotes());
    response.setAllocatedAmount(allocatedAmount);
    response.setUnallocatedAmount(unallocatedAmount);
    response.setPartyCreditAfter(
        partyRepository.findByOrgIdAndId(settlement.getOrgId(), settlement.getPartyId())
            .map(Party::getCreditAmount)
            .map(this::normalizeAmount)
            .orElse(BigDecimal.ZERO));
    response.setCreatedAt(settlement.getCreatedAt());
    return response;
  }

  private SettlementAllocationResponse toAllocationResponse(SettlementAllocation allocation, BigDecimal pendingAmount) {
    SettlementAllocationResponse response = new SettlementAllocationResponse();
    response.setId(allocation.getId());
    response.setSettlementId(allocation.getSettlementId());
    response.setTripId(allocation.getTripId());
    response.setAmountApplied(allocation.getAllocatedAmount());
    response.setPendingAmount(pendingAmount);
    response.setCreatedAt(allocation.getCreatedAt());
    return response;
  }

  private String normalizeMode(String mode) {
    if (mode == null) {
      throw new BadRequestException("Payment mode is required");
    }
    String normalized = mode.trim().toUpperCase();
    if (!ALLOWED_MODES.contains(normalized)) {
      throw new BadRequestException("Payment mode must be CASH, UPI, BANK, or OTHER");
    }
    return normalized;
  }

  private String generateSettlementCode(UUID orgId) {
    for (int i = 0; i < 5; i++) {
      String code = "SET-" + System.currentTimeMillis();
      if (settlementRepository.findByOrgIdAndSettlementCode(orgId, code).isEmpty()) {
        return code;
      }
    }
    return "SET-" + UUID.randomUUID();
  }

  private BigDecimal sumAllocations(List<SettlementAllocation> allocations) {
    return allocations.stream()
        .map(SettlementAllocation::getAllocatedAmount)
        .filter(amount -> amount != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal normalizeAmount(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  @Override
  public BigDecimal partyCredit(UUID orgId, UUID partyId) {
    Party party = partyRepository.findByOrgIdAndId(orgId, partyId)
        .orElseThrow(() -> new NotFoundException("Party not found"));
    return normalizeAmount(party.getCreditAmount());
  }

  // Credit consumption is tracked at party level; do not mutate older settlements.

  private String resolveBillingStatus(BigDecimal freight, BigDecimal paid) {
    if (paid.compareTo(BigDecimal.ZERO) == 0) {
      return "UNPAID";
    }
    if (paid.compareTo(freight) < 0) {
      return "PARTIALLY_PAID";
    }
    return "PAID";
  }
}
