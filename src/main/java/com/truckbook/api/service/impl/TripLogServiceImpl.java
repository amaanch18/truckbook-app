package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.DriverExpenseCreateRequest;
import com.truckbook.api.controller.dto.DriverExpenseResponse;
import com.truckbook.api.controller.dto.DriverExpenseCategory;
import com.truckbook.api.controller.dto.DriverExpenseUpdateRequest;
import com.truckbook.api.controller.dto.FuelLogCreateRequest;
import com.truckbook.api.controller.dto.FuelLogResponse;
import com.truckbook.api.controller.dto.FuelLogUpdateRequest;
import com.truckbook.api.controller.dto.TollCreateRequest;
import com.truckbook.api.controller.dto.TollResponse;
import com.truckbook.api.controller.dto.TollUpdateRequest;
import com.truckbook.api.entity.Trip;
import com.truckbook.api.entity.TripDriverExpense;
import com.truckbook.api.entity.TripFuelLog;
import com.truckbook.api.entity.TripTollLog;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.TripDriverExpenseRepository;
import com.truckbook.api.repository.TripFuelLogRepository;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TripTollLogRepository;
import com.truckbook.api.service.TripLogService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TripLogServiceImpl implements TripLogService {
  private final TripRepository tripRepository;
  private final TripFuelLogRepository fuelLogRepository;
  private final TripTollLogRepository tollLogRepository;
  private final TripDriverExpenseRepository driverExpenseRepository;

  public TripLogServiceImpl(
      TripRepository tripRepository,
      TripFuelLogRepository fuelLogRepository,
      TripTollLogRepository tollLogRepository,
      TripDriverExpenseRepository driverExpenseRepository) {
    this.tripRepository = tripRepository;
    this.fuelLogRepository = fuelLogRepository;
    this.tollLogRepository = tollLogRepository;
    this.driverExpenseRepository = driverExpenseRepository;
  }

  @Override
  public FuelLogResponse createFuel(UUID orgId, UUID tripId, FuelLogCreateRequest request) {
    Trip trip = requireTrip(orgId, tripId);
    BigDecimal amount = computeFuelAmount(request.getLiters(), request.getRatePerLiter());

    TripFuelLog log = new TripFuelLog();
    log.setId(UUID.randomUUID());
    log.setOrgId(orgId);
    log.setTripId(trip.getId());
    log.setEntryDate(request.getFilledOn());
    log.setLiters(request.getLiters());
    log.setAmount(amount);
    log.setOdometerKm(request.getOdometerKm() == null ? null : new BigDecimal(request.getOdometerKm()));
    log.setNotes(mergeFuelNotes(request.getFuelStation(), request.getNotes()));
    log.setCreatedAt(OffsetDateTime.now());

    return toFuelResponse(fuelLogRepository.save(log));
  }

  @Override
  public List<FuelLogResponse> listFuel(UUID orgId, UUID tripId) {
    requireTrip(orgId, tripId);
    return fuelLogRepository.findAllByOrgIdAndTripIdOrderByEntryDateDesc(orgId, tripId)
        .stream()
        .map(this::toFuelResponse)
        .toList();
  }

  @Override
  public FuelLogResponse updateFuel(UUID orgId, UUID tripId, UUID fuelId, FuelLogUpdateRequest request) {
    requireTrip(orgId, tripId);
    TripFuelLog log = fuelLogRepository.findByOrgIdAndTripIdAndId(orgId, tripId, fuelId)
        .orElseThrow(() -> new NotFoundException("Fuel log not found"));

    if (request.getFilledOn() != null) {
      log.setEntryDate(request.getFilledOn());
    }
    boolean litersUpdated = false;
    if (request.getLiters() != null) {
      log.setLiters(request.getLiters());
      litersUpdated = true;
    }
    if (request.getRatePerLiter() != null) {
      if (log.getLiters() == null) {
        throw new BadRequestException("Liters is required to update rate per liter");
      }
      log.setAmount(computeFuelAmount(log.getLiters(), request.getRatePerLiter()));
    } else if (litersUpdated) {
      throw new BadRequestException("ratePerLiter is required when updating liters");
    }
    if (request.getOdometerKm() != null) {
      log.setOdometerKm(new BigDecimal(request.getOdometerKm()));
    }
    if (request.getFuelStation() != null || request.getNotes() != null) {
      String existingStation = extractFuelStation(log.getNotes());
      String existingNotes = extractFuelNotes(log.getNotes());
      String newStation = request.getFuelStation() != null ? request.getFuelStation() : existingStation;
      String newNotes = request.getNotes() != null ? request.getNotes() : existingNotes;
      log.setNotes(mergeFuelNotes(newStation, newNotes));
    }

    return toFuelResponse(fuelLogRepository.save(log));
  }

  @Override
  public void deleteFuel(UUID orgId, UUID tripId, UUID fuelId) {
    requireTrip(orgId, tripId);
    TripFuelLog log = fuelLogRepository.findByOrgIdAndTripIdAndId(orgId, tripId, fuelId)
        .orElseThrow(() -> new NotFoundException("Fuel log not found"));
    fuelLogRepository.delete(log);
  }

  @Override
  public TollResponse createToll(UUID orgId, UUID tripId, TollCreateRequest request) {
    Trip trip = requireTrip(orgId, tripId);
    TripTollLog log = new TripTollLog();
    log.setId(UUID.randomUUID());
    log.setOrgId(orgId);
    log.setTripId(trip.getId());
    log.setEntryDate(request.getPaidOn());
    log.setPlaza(request.getPlazaName());
    log.setAmount(request.getAmount());
    log.setNotes(request.getNotes());
    log.setCreatedAt(OffsetDateTime.now());

    return toTollResponse(tollLogRepository.save(log));
  }

  @Override
  public List<TollResponse> listTolls(UUID orgId, UUID tripId) {
    requireTrip(orgId, tripId);
    return tollLogRepository.findAllByOrgIdAndTripIdOrderByEntryDateDesc(orgId, tripId)
        .stream()
        .map(this::toTollResponse)
        .toList();
  }

  @Override
  public TollResponse updateToll(UUID orgId, UUID tripId, UUID tollId, TollUpdateRequest request) {
    requireTrip(orgId, tripId);
    TripTollLog log = tollLogRepository.findByOrgIdAndTripIdAndId(orgId, tripId, tollId)
        .orElseThrow(() -> new NotFoundException("Toll log not found"));

    if (request.getPaidOn() != null) {
      log.setEntryDate(request.getPaidOn());
    }
    if (request.getAmount() != null) {
      log.setAmount(request.getAmount());
    }
    if (request.getPlazaName() != null) {
      log.setPlaza(request.getPlazaName());
    }
    if (request.getNotes() != null) {
      log.setNotes(request.getNotes());
    }

    return toTollResponse(tollLogRepository.save(log));
  }

  @Override
  public void deleteToll(UUID orgId, UUID tripId, UUID tollId) {
    requireTrip(orgId, tripId);
    TripTollLog log = tollLogRepository.findByOrgIdAndTripIdAndId(orgId, tripId, tollId)
        .orElseThrow(() -> new NotFoundException("Toll log not found"));
    tollLogRepository.delete(log);
  }

  @Override
  public DriverExpenseResponse createDriverExpense(UUID orgId, UUID tripId, DriverExpenseCreateRequest request) {
    Trip trip = requireTrip(orgId, tripId);
    TripDriverExpense log = new TripDriverExpense();
    log.setId(UUID.randomUUID());
    log.setOrgId(orgId);
    log.setTripId(trip.getId());
    log.setEntryDate(request.getSpentOn());
    log.setCategory(validateCategory(request.getCategory()));
    log.setAmount(request.getAmount());
    log.setNotes(request.getNotes());
    log.setCreatedAt(OffsetDateTime.now());

    return toDriverExpenseResponse(driverExpenseRepository.save(log));
  }

  @Override
  public List<DriverExpenseResponse> listDriverExpenses(UUID orgId, UUID tripId) {
    requireTrip(orgId, tripId);
    return driverExpenseRepository.findAllByOrgIdAndTripIdOrderByEntryDateDesc(orgId, tripId)
        .stream()
        .map(this::toDriverExpenseResponse)
        .toList();
  }

  @Override
  public DriverExpenseResponse updateDriverExpense(
      UUID orgId,
      UUID tripId,
      UUID expenseId,
      DriverExpenseUpdateRequest request) {
    requireTrip(orgId, tripId);
    TripDriverExpense log = driverExpenseRepository.findByOrgIdAndTripIdAndId(orgId, tripId, expenseId)
        .orElseThrow(() -> new NotFoundException("Driver expense not found"));

    if (request.getSpentOn() != null) {
      log.setEntryDate(request.getSpentOn());
    }
    if (request.getCategory() != null) {
      log.setCategory(validateCategory(request.getCategory()));
    }
    if (request.getAmount() != null) {
      log.setAmount(request.getAmount());
    }
    if (request.getNotes() != null) {
      log.setNotes(request.getNotes());
    }

    return toDriverExpenseResponse(driverExpenseRepository.save(log));
  }

  @Override
  public void deleteDriverExpense(UUID orgId, UUID tripId, UUID expenseId) {
    requireTrip(orgId, tripId);
    TripDriverExpense log = driverExpenseRepository.findByOrgIdAndTripIdAndId(orgId, tripId, expenseId)
        .orElseThrow(() -> new NotFoundException("Driver expense not found"));
    driverExpenseRepository.delete(log);
  }

  @Override
  public BigDecimal fuelTotal(UUID orgId, UUID tripId) {
    return normalizeAmount(fuelLogRepository.sumAmountByOrgIdAndTripId(orgId, tripId));
  }

  @Override
  public BigDecimal tollTotal(UUID orgId, UUID tripId) {
    return normalizeAmount(tollLogRepository.sumAmountByOrgIdAndTripId(orgId, tripId));
  }

  @Override
  public BigDecimal driverExpenseTotal(UUID orgId, UUID tripId) {
    return normalizeAmount(driverExpenseRepository.sumAmountByOrgIdAndTripId(orgId, tripId));
  }

  private Trip requireTrip(UUID orgId, UUID tripId) {
    return tripRepository.findByOrgIdAndId(orgId, tripId)
        .orElseThrow(() -> new NotFoundException("Trip not found"));
  }

  private BigDecimal computeFuelAmount(BigDecimal liters, BigDecimal ratePerLiter) {
    if (liters == null || ratePerLiter == null) {
      throw new BadRequestException("Liters and ratePerLiter are required");
    }
    return liters.multiply(ratePerLiter).setScale(2, RoundingMode.HALF_UP);
  }

  private String validateCategory(DriverExpenseCategory category) {
    if (category == null) {
      throw new BadRequestException("Category is required");
    }
    return category.name();
  }

  private FuelLogResponse toFuelResponse(TripFuelLog log) {
    FuelLogResponse response = new FuelLogResponse();
    response.setId(log.getId());
    response.setTripId(log.getTripId());
    response.setFilledOn(log.getEntryDate());
    response.setLiters(log.getLiters());
    response.setAmount(log.getAmount());
    response.setRatePerLiter(deriveRate(log.getLiters(), log.getAmount()));
    response.setFuelStation(extractFuelStation(log.getNotes()));
    response.setOdometerKm(log.getOdometerKm());
    response.setNotes(extractFuelNotes(log.getNotes()));
    response.setCreatedAt(log.getCreatedAt());
    return response;
  }

  private TollResponse toTollResponse(TripTollLog log) {
    TollResponse response = new TollResponse();
    response.setId(log.getId());
    response.setTripId(log.getTripId());
    response.setPaidOn(log.getEntryDate());
    response.setAmount(log.getAmount());
    response.setPlazaName(log.getPlaza());
    response.setNotes(log.getNotes());
    response.setCreatedAt(log.getCreatedAt());
    return response;
  }

  private DriverExpenseResponse toDriverExpenseResponse(TripDriverExpense log) {
    DriverExpenseResponse response = new DriverExpenseResponse();
    response.setId(log.getId());
    response.setTripId(log.getTripId());
    response.setSpentOn(log.getEntryDate());
    response.setCategory(log.getCategory());
    response.setAmount(log.getAmount());
    response.setNotes(log.getNotes());
    response.setCreatedAt(log.getCreatedAt());
    return response;
  }

  private BigDecimal deriveRate(BigDecimal liters, BigDecimal amount) {
    if (liters == null || amount == null || liters.compareTo(BigDecimal.ZERO) == 0) {
      return null;
    }
    return amount.divide(liters, 2, RoundingMode.HALF_UP);
  }

  private String mergeFuelNotes(String fuelStation, String notes) {
    String station = fuelStation == null ? "" : fuelStation.trim();
    String noteText = notes == null ? "" : notes.trim();
    if (station.isEmpty() && noteText.isEmpty()) {
      return null;
    }
    if (station.isEmpty()) {
      return noteText;
    }
    if (noteText.isEmpty()) {
      return "Station=" + station;
    }
    return "Station=" + station + "\n" + noteText;
  }

  private String extractFuelStation(String storedNotes) {
    if (storedNotes == null || storedNotes.isBlank()) {
      return null;
    }
    String[] lines = storedNotes.split("\\n", 2);
    if (lines.length > 0 && lines[0].startsWith("Station=")) {
      return lines[0].substring("Station=".length());
    }
    return null;
  }

  private String extractFuelNotes(String storedNotes) {
    if (storedNotes == null || storedNotes.isBlank()) {
      return null;
    }
    String[] lines = storedNotes.split("\\n", 2);
    if (lines.length > 0 && lines[0].startsWith("Station=")) {
      if (lines.length == 2 && !lines[1].isBlank()) {
        return lines[1];
      }
      return null;
    }
    return storedNotes;
  }

  private BigDecimal normalizeAmount(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }
}
