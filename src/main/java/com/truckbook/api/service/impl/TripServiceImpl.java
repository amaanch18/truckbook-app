package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.TripCreateRequest;
import com.truckbook.api.controller.dto.TripResponse;
import com.truckbook.api.controller.dto.TripUpdateRequest;
import com.truckbook.api.entity.Party;
import com.truckbook.api.entity.Trip;
import com.truckbook.api.entity.Truck;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.exception.ConflictException;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.PartyRepository;
import com.truckbook.api.repository.SettlementAllocationRepository;
import com.truckbook.api.repository.TripAllocationSum;
import com.truckbook.api.repository.TripAmountSum;
import com.truckbook.api.repository.TripDriverExpenseRepository;
import com.truckbook.api.repository.TripFuelLogRepository;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TripSpecifications;
import com.truckbook.api.repository.TripTollLogRepository;
import com.truckbook.api.repository.TruckRepository;
import com.truckbook.api.service.TripService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {
  private final TripRepository tripRepository;
  private final TruckRepository truckRepository;
  private final PartyRepository partyRepository;
  private final SettlementAllocationRepository settlementAllocationRepository;
  private final TripFuelLogRepository fuelLogRepository;
  private final TripTollLogRepository tollLogRepository;
  private final TripDriverExpenseRepository driverExpenseRepository;

  public TripServiceImpl(
      TripRepository tripRepository,
      TruckRepository truckRepository,
      PartyRepository partyRepository,
      SettlementAllocationRepository settlementAllocationRepository,
      TripFuelLogRepository fuelLogRepository,
      TripTollLogRepository tollLogRepository,
      TripDriverExpenseRepository driverExpenseRepository) {
    this.tripRepository = tripRepository;
    this.truckRepository = truckRepository;
    this.partyRepository = partyRepository;
    this.settlementAllocationRepository = settlementAllocationRepository;
    this.fuelLogRepository = fuelLogRepository;
    this.tollLogRepository = tollLogRepository;
    this.driverExpenseRepository = driverExpenseRepository;
  }

  @Override
  public TripResponse create(UUID orgId, TripCreateRequest request) {
    if (request.getTruckId() == null) {
      throw new BadRequestException("Truck is required to create a trip");
    }

    ensureTripCodeUnique(orgId, request.getTripCode(), null);

    Truck truck = truckRepository.findByOrgIdAndId(orgId, request.getTruckId())
        .orElseThrow(() -> new NotFoundException("Truck not found"));
    if ("INACTIVE".equalsIgnoreCase(truck.getStatus())) {
      throw new BadRequestException("Inactive truck cannot be assigned");
    }

    Party party = null;
    if (request.getPartyId() != null) {
      party = partyRepository.findByOrgIdAndId(orgId, request.getPartyId())
          .orElseThrow(() -> new NotFoundException("Party not found"));
    }

    OffsetDateTime now = OffsetDateTime.now();
    Trip trip = new Trip();
    trip.setId(UUID.randomUUID());
    trip.setOrgId(orgId);
    trip.setTruckId(truck.getId());
    trip.setPartyId(party == null ? null : party.getId());
    trip.setTripCode(request.getTripCode());
    trip.setStatus("ACTIVE");
    trip.setDriverName(request.getDriverName());
    trip.setFromLocation(request.getFromLocation());
    trip.setToLocation(request.getToLocation());
    trip.setStartDate(request.getStartDate());
    trip.setFreightAmount(request.getFreightAmount());
    trip.setNotes(request.getNotes());
    trip.setCreatedAt(now);
    trip.setUpdatedAt(now);
    Trip saved = tripRepository.save(trip);
    return toResponse(saved, BigDecimal.ZERO);
  }

  @Override
  public TripResponse get(UUID orgId, UUID id) {
    Trip trip = tripRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Trip not found"));
    BigDecimal paidAmount = settlementAllocationRepository
        .sumAppliedByOrgIdAndTripId(orgId, trip.getId());
    TripResponse response = toResponse(trip, paidAmount);
    applyExpenseTotals(orgId, trip.getId(), response);
    return response;
  }

  @Override
  public TripResponse update(UUID orgId, UUID id, TripUpdateRequest request) {
    Trip trip = tripRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Trip not found"));

    if ("COMPLETED".equalsIgnoreCase(trip.getStatus())) {
      boolean blockedChange =
          request.getPartyId() != null
              || request.getDriverName() != null
              || request.getFromLocation() != null
              || request.getToLocation() != null
              || request.getStartDate() != null
              || request.getFreightAmount() != null;
      if (blockedChange) {
        throw new ConflictException("Completed trips can only update notes");
      }

      trip.setNotes(request.getNotes());
      trip.setUpdatedAt(OffsetDateTime.now());
      Trip saved = tripRepository.save(trip);
      BigDecimal paidAmount = settlementAllocationRepository
          .sumAppliedByOrgIdAndTripId(orgId, saved.getId());
      return toResponse(saved, paidAmount);
    }

    Party party = null;
    if (request.getPartyId() != null) {
      party = partyRepository.findByOrgIdAndId(orgId, request.getPartyId())
          .orElseThrow(() -> new NotFoundException("Party not found"));
    }

    if (request.getPartyId() != null) {
      trip.setPartyId(party == null ? null : party.getId());
    }
    if (request.getDriverName() != null) {
      trip.setDriverName(request.getDriverName());
    }
    if (request.getFromLocation() != null) {
      trip.setFromLocation(request.getFromLocation());
    }
    if (request.getToLocation() != null) {
      trip.setToLocation(request.getToLocation());
    }
    if (request.getStartDate() != null) {
      trip.setStartDate(request.getStartDate());
    }
    if (request.getFreightAmount() != null) {
      trip.setFreightAmount(request.getFreightAmount());
    }
    if (request.getNotes() != null) {
      trip.setNotes(request.getNotes());
    }
    trip.setUpdatedAt(OffsetDateTime.now());
    Trip saved = tripRepository.save(trip);
    BigDecimal paidAmount = settlementAllocationRepository
        .sumAppliedByOrgIdAndTripId(orgId, saved.getId());
    return toResponse(saved, paidAmount);
  }

  @Override
  public TripResponse complete(UUID orgId, UUID id) {
    Trip trip = tripRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Trip not found"));
    if (!"COMPLETED".equalsIgnoreCase(trip.getStatus())) {
      trip.setStatus("COMPLETED");
      trip.setUpdatedAt(OffsetDateTime.now());
      trip = tripRepository.save(trip);
    }
    BigDecimal paidAmount = settlementAllocationRepository
        .sumAppliedByOrgIdAndTripId(orgId, trip.getId());
    return toResponse(trip, paidAmount);
  }

  @Override
  public String delete(UUID orgId, UUID id) {
    Trip trip = tripRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Trip not found"));

    if ("COMPLETED".equalsIgnoreCase(trip.getStatus())) {
      throw new ConflictException("Completed trips cannot be deleted");
    }

    if (settlementAllocationRepository.existsByOrgIdAndTripId(orgId, id)) {
      throw new ConflictException("Trip has settlements and cannot be deleted");
    }

    tripRepository.delete(trip);
    return "Trip deleted successfully";
  }

  @Override
  public List<TripResponse> list(
      UUID orgId,
      String status,
      UUID truckId,
      UUID partyId,
      LocalDate dateFrom,
      LocalDate dateTo) {
    Specification<Trip> spec = Specification.where(TripSpecifications.orgId(orgId));
    if (status != null && !status.isBlank()) {
      spec = spec.and(TripSpecifications.status(normalizeStatus(status)));
    }
    if (truckId != null) {
      spec = spec.and(TripSpecifications.truckId(truckId));
    }
    if (partyId != null) {
      spec = spec.and(TripSpecifications.partyId(partyId));
    }
    if (dateFrom != null) {
      spec = spec.and(TripSpecifications.startDateFrom(dateFrom));
    }
    if (dateTo != null) {
      spec = spec.and(TripSpecifications.startDateTo(dateTo));
    }

    List<Trip> trips = tripRepository.findAll(spec);
    Map<UUID, BigDecimal> paidByTrip = loadPaidAmounts(orgId, trips);
    List<UUID> tripIds = trips.stream().map(Trip::getId).toList();
    Map<UUID, BigDecimal> fuelTotals = loadTripTotals(
        fuelLogRepository.sumAmountByOrgIdAndTripIdIn(orgId, tripIds));
    Map<UUID, BigDecimal> tollTotals = loadTripTotals(
        tollLogRepository.sumAmountByOrgIdAndTripIdIn(orgId, tripIds));
    Map<UUID, BigDecimal> driverTotals = loadTripTotals(
        driverExpenseRepository.sumAmountByOrgIdAndTripIdIn(orgId, tripIds));

    return trips.stream()
        .map(trip -> {
          TripResponse response = toResponse(trip, paidByTrip.get(trip.getId()));
          applyExpenseTotals(
              fuelTotals.get(trip.getId()),
              tollTotals.get(trip.getId()),
              driverTotals.get(trip.getId()),
              response);
          return response;
        })
        .toList();
  }

  private void ensureTripCodeUnique(UUID orgId, String tripCode, UUID excludeTripId) {
    tripRepository.findByOrgIdAndTripCode(orgId, tripCode)
        .ifPresent(existing -> {
          if (excludeTripId == null || !existing.getId().equals(excludeTripId)) {
            throw new ConflictException("Trip code already exists for this organization");
          }
        });
  }

  private String normalizeStatus(String status) {
    String normalized = status.toUpperCase();
    if (!"ACTIVE".equals(normalized) && !"COMPLETED".equals(normalized)) {
      throw new BadRequestException("Status must be ACTIVE or COMPLETED");
    }
    return normalized;
  }

  private TripResponse toResponse(Trip trip, BigDecimal paidAmountRaw) {
    BigDecimal paidAmount = normalizeAmount(paidAmountRaw);
    BigDecimal freightAmount = normalizeAmount(trip.getFreightAmount());
    BigDecimal outstandingAmount = freightAmount.subtract(paidAmount);
    if (outstandingAmount.signum() < 0) {
      outstandingAmount = BigDecimal.ZERO;
    }

    String billingStatus;
    if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
      billingStatus = "UNPAID";
    } else if (paidAmount.compareTo(freightAmount) < 0) {
      billingStatus = "PARTIALLY_PAID";
    } else {
      billingStatus = "PAID";
    }

    TripResponse response = new TripResponse();
    response.setId(trip.getId());
    response.setOrgId(trip.getOrgId());
    response.setTruckId(trip.getTruckId());
    response.setPartyId(trip.getPartyId());
    response.setTripCode(trip.getTripCode());
    response.setStatus(trip.getStatus());
    response.setDriverName(trip.getDriverName());
    response.setFromLocation(trip.getFromLocation());
    response.setToLocation(trip.getToLocation());
    response.setStartDate(trip.getStartDate());
    response.setFreightAmount(trip.getFreightAmount());
    response.setPaidAmount(paidAmount);
    response.setOutstandingAmount(outstandingAmount);
    response.setBillingStatus(billingStatus);
    response.setFuelTotal(BigDecimal.ZERO);
    response.setTollTotal(BigDecimal.ZERO);
    response.setDriverExpenseTotal(BigDecimal.ZERO);
    response.setTotalExpense(BigDecimal.ZERO);
    response.setNotes(trip.getNotes());
    response.setCreatedAt(trip.getCreatedAt());
    response.setUpdatedAt(trip.getUpdatedAt());
    return response;
  }

  private Map<UUID, BigDecimal> loadPaidAmounts(UUID orgId, List<Trip> trips) {
    if (trips.isEmpty()) {
      return java.util.Collections.emptyMap();
    }
    List<UUID> tripIds = trips.stream().map(Trip::getId).toList();
    return settlementAllocationRepository.sumAppliedByOrgIdAndTripIdIn(orgId, tripIds).stream()
        .collect(Collectors.toMap(TripAllocationSum::getTripId, sum -> normalizeAmount(sum.getTotalApplied())));
  }

  private BigDecimal normalizeAmount(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private void applyExpenseTotals(UUID orgId, UUID tripId, TripResponse response) {
    BigDecimal fuelTotal = normalizeAmount(fuelLogRepository.sumAmountByOrgIdAndTripId(orgId, tripId));
    BigDecimal tollTotal = normalizeAmount(tollLogRepository.sumAmountByOrgIdAndTripId(orgId, tripId));
    BigDecimal driverExpenseTotal =
        normalizeAmount(driverExpenseRepository.sumAmountByOrgIdAndTripId(orgId, tripId));
    applyExpenseTotals(fuelTotal, tollTotal, driverExpenseTotal, response);
  }

  private void applyExpenseTotals(
      BigDecimal fuelTotal,
      BigDecimal tollTotal,
      BigDecimal driverExpenseTotal,
      TripResponse response) {
    BigDecimal fuel = normalizeAmount(fuelTotal);
    BigDecimal toll = normalizeAmount(tollTotal);
    BigDecimal driver = normalizeAmount(driverExpenseTotal);
    BigDecimal totalExpense = fuel.add(toll).add(driver);
    response.setFuelTotal(fuel);
    response.setTollTotal(toll);
    response.setDriverExpenseTotal(driver);
    response.setTotalExpense(totalExpense);
  }

  private Map<UUID, BigDecimal> loadTripTotals(List<TripAmountSum> sums) {
    if (sums.isEmpty()) {
      return java.util.Collections.emptyMap();
    }
    return sums.stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeAmount(sum.getTotalAmount())));
  }
}
