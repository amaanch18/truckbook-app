package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.truckcosts.RepairCreateRequest;
import com.truckbook.api.controller.dto.truckcosts.RepairResponse;
import com.truckbook.api.controller.dto.truckcosts.RepairUpdateRequest;
import com.truckbook.api.controller.dto.truckcosts.TruckCostSummaryResponse;
import com.truckbook.api.controller.dto.truckcosts.TyreCreateRequest;
import com.truckbook.api.controller.dto.truckcosts.TyreResponse;
import com.truckbook.api.controller.dto.truckcosts.TyreUpdateRequest;
import com.truckbook.api.entity.Truck;
import com.truckbook.api.entity.TruckRepair;
import com.truckbook.api.entity.TruckTyreExpense;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.TruckRepairRepository;
import com.truckbook.api.repository.TruckRepository;
import com.truckbook.api.repository.TruckTyreExpenseRepository;
import com.truckbook.api.service.TruckCostService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TruckCostServiceImpl implements TruckCostService {
  private final TruckRepository truckRepository;
  private final TruckRepairRepository truckRepairRepository;
  private final TruckTyreExpenseRepository truckTyreExpenseRepository;

  public TruckCostServiceImpl(
      TruckRepository truckRepository,
      TruckRepairRepository truckRepairRepository,
      TruckTyreExpenseRepository truckTyreExpenseRepository) {
    this.truckRepository = truckRepository;
    this.truckRepairRepository = truckRepairRepository;
    this.truckTyreExpenseRepository = truckTyreExpenseRepository;
  }

  @Override
  public RepairResponse createRepair(UUID orgId, UUID truckId, RepairCreateRequest request) {
    Truck truck = requireTruck(orgId, truckId);
    TruckRepair repair = new TruckRepair();
    repair.setId(UUID.randomUUID());
    repair.setOrgId(orgId);
    repair.setTruckId(truck.getId());
    repair.setEntryDate(request.getRepairedOn());
    repair.setAmount(request.getAmount());
    repair.setVendor(request.getVendorName());
    repair.setDescription(buildDescription(request.getDescription(), request.getNotes()));
    repair.setOdometerKm(request.getOdometerKm() == null ? null : new BigDecimal(request.getOdometerKm()));
    repair.setCreatedAt(OffsetDateTime.now());

    return toRepairResponse(truckRepairRepository.save(repair));
  }

  @Override
  public List<RepairResponse> listRepairs(UUID orgId, UUID truckId, LocalDate from, LocalDate to) {
    requireTruck(orgId, truckId);
    DateRange range = normalizeRange(from, to);
    List<TruckRepair> repairs = range == null
        ? truckRepairRepository.findAllByOrgIdAndTruckIdOrderByEntryDateDesc(orgId, truckId)
        : truckRepairRepository.findAllByOrgIdAndTruckIdAndEntryDateBetweenOrderByEntryDateDesc(
            orgId, truckId, range.from, range.to);
    return repairs.stream().map(this::toRepairResponse).toList();
  }

  @Override
  public RepairResponse updateRepair(UUID orgId, UUID truckId, UUID repairId, RepairUpdateRequest request) {
    requireTruck(orgId, truckId);
    TruckRepair repair = truckRepairRepository.findByOrgIdAndTruckIdAndId(orgId, truckId, repairId)
        .orElseThrow(() -> new NotFoundException("Repair not found"));

    if (request.getRepairedOn() != null) {
      repair.setEntryDate(request.getRepairedOn());
    }
    if (request.getAmount() != null) {
      repair.setAmount(request.getAmount());
    }
    if (request.getVendorName() != null) {
      repair.setVendor(request.getVendorName());
    }
    if (request.getDescription() != null || request.getNotes() != null) {
      String currentNotes = extractNotes(repair.getDescription());
      String currentDesc = extractDescription(repair.getDescription());
      String nextDesc = request.getDescription() != null ? request.getDescription() : currentDesc;
      String nextNotes = request.getNotes() != null ? request.getNotes() : currentNotes;
      repair.setDescription(buildDescription(nextDesc, nextNotes));
    }
    if (request.getOdometerKm() != null) {
      repair.setOdometerKm(new BigDecimal(request.getOdometerKm()));
    }

    return toRepairResponse(truckRepairRepository.save(repair));
  }

  @Override
  public void deleteRepair(UUID orgId, UUID truckId, UUID repairId) {
    requireTruck(orgId, truckId);
    TruckRepair repair = truckRepairRepository.findByOrgIdAndTruckIdAndId(orgId, truckId, repairId)
        .orElseThrow(() -> new NotFoundException("Repair not found"));
    truckRepairRepository.delete(repair);
  }

  @Override
  public TyreResponse createTyre(UUID orgId, UUID truckId, TyreCreateRequest request) {
    Truck truck = requireTruck(orgId, truckId);
    TruckTyreExpense tyre = new TruckTyreExpense();
    tyre.setId(UUID.randomUUID());
    tyre.setOrgId(orgId);
    tyre.setTruckId(truck.getId());
    tyre.setEntryDate(request.getPurchasedOn());
    tyre.setAmount(request.getAmount());
    tyre.setTyrePosition(request.getBrand());
    tyre.setDescription(buildTyreDescription(request.getTyreCount(), request.getNotes()));
    tyre.setCreatedAt(OffsetDateTime.now());

    return toTyreResponse(truckTyreExpenseRepository.save(tyre));
  }

  @Override
  public List<TyreResponse> listTyres(UUID orgId, UUID truckId, LocalDate from, LocalDate to) {
    requireTruck(orgId, truckId);
    DateRange range = normalizeRange(from, to);
    List<TruckTyreExpense> tyres = range == null
        ? truckTyreExpenseRepository.findAllByOrgIdAndTruckIdOrderByEntryDateDesc(orgId, truckId)
        : truckTyreExpenseRepository.findAllByOrgIdAndTruckIdAndEntryDateBetweenOrderByEntryDateDesc(
            orgId, truckId, range.from, range.to);
    return tyres.stream().map(this::toTyreResponse).toList();
  }

  @Override
  public TyreResponse updateTyre(UUID orgId, UUID truckId, UUID tyreId, TyreUpdateRequest request) {
    requireTruck(orgId, truckId);
    TruckTyreExpense tyre = truckTyreExpenseRepository.findByOrgIdAndTruckIdAndId(orgId, truckId, tyreId)
        .orElseThrow(() -> new NotFoundException("Tyre expense not found"));

    if (request.getPurchasedOn() != null) {
      tyre.setEntryDate(request.getPurchasedOn());
    }
    if (request.getAmount() != null) {
      tyre.setAmount(request.getAmount());
    }
    if (request.getBrand() != null) {
      tyre.setTyrePosition(request.getBrand());
    }
    if (request.getNotes() != null || request.getTyreCount() != null) {
      Integer currentCount = extractTyreCount(tyre.getDescription());
      String currentNotes = extractTyreNotes(tyre.getDescription());
      Integer nextCount = request.getTyreCount() != null ? request.getTyreCount() : currentCount;
      String nextNotes = request.getNotes() != null ? request.getNotes() : currentNotes;
      tyre.setDescription(buildTyreDescription(nextCount, nextNotes));
    }

    return toTyreResponse(truckTyreExpenseRepository.save(tyre));
  }

  @Override
  public void deleteTyre(UUID orgId, UUID truckId, UUID tyreId) {
    requireTruck(orgId, truckId);
    TruckTyreExpense tyre = truckTyreExpenseRepository.findByOrgIdAndTruckIdAndId(orgId, truckId, tyreId)
        .orElseThrow(() -> new NotFoundException("Tyre expense not found"));
    truckTyreExpenseRepository.delete(tyre);
  }

  @Override
  public TruckCostSummaryResponse summary(UUID orgId, UUID truckId, LocalDate from, LocalDate to) {
    requireTruck(orgId, truckId);
    DateRange range = normalizeRange(from, to);
    if (range == null) {
      throw new BadRequestException("from and to are required");
    }

    BigDecimal repairs = normalizeAmount(
        truckRepairRepository.sumByOrgIdTruckIdAndDateRange(orgId, truckId, range.from, range.to));
    BigDecimal tyres = normalizeAmount(
        truckTyreExpenseRepository.sumByOrgIdTruckIdAndDateRange(orgId, truckId, range.from, range.to));
    BigDecimal total = repairs.add(tyres);

    TruckCostSummaryResponse response = new TruckCostSummaryResponse();
    response.setTruckId(truckId);
    response.setFrom(range.from);
    response.setTo(range.to);
    response.setRepairsTotal(repairs);
    response.setTyresTotal(tyres);
    response.setTotal(total);
    return response;
  }

  private Truck requireTruck(UUID orgId, UUID truckId) {
    return truckRepository.findByOrgIdAndId(orgId, truckId)
        .orElseThrow(() -> new NotFoundException("Truck not found"));
  }

  private DateRange normalizeRange(LocalDate from, LocalDate to) {
    if (from == null && to == null) {
      return null;
    }
    LocalDate start = from == null ? to : from;
    LocalDate end = to == null ? from : to;
    if (start.isAfter(end)) {
      throw new BadRequestException("from must be on or before to");
    }
    return new DateRange(start, end);
  }

  private RepairResponse toRepairResponse(TruckRepair repair) {
    RepairResponse response = new RepairResponse();
    response.setId(repair.getId());
    response.setTruckId(repair.getTruckId());
    response.setRepairedOn(repair.getEntryDate());
    response.setAmount(repair.getAmount());
    response.setVendorName(repair.getVendor());
    response.setDescription(extractDescription(repair.getDescription()));
    response.setNotes(extractNotes(repair.getDescription()));
    response.setOdometerKm(repair.getOdometerKm() == null ? null : repair.getOdometerKm().intValue());
    response.setCreatedAt(repair.getCreatedAt());
    return response;
  }

  private TyreResponse toTyreResponse(TruckTyreExpense tyre) {
    TyreResponse response = new TyreResponse();
    response.setId(tyre.getId());
    response.setTruckId(tyre.getTruckId());
    response.setPurchasedOn(tyre.getEntryDate());
    response.setAmount(tyre.getAmount());
    response.setBrand(tyre.getTyrePosition());
    response.setTyreCount(extractTyreCount(tyre.getDescription()));
    response.setNotes(extractTyreNotes(tyre.getDescription()));
    response.setCreatedAt(tyre.getCreatedAt());
    return response;
  }

  private String buildDescription(String description, String notes) {
    String desc = description == null ? "" : description.trim();
    String noteText = notes == null ? "" : notes.trim();
    if (desc.isEmpty() && noteText.isEmpty()) {
      return null;
    }
    if (desc.isEmpty()) {
      return noteText;
    }
    if (noteText.isEmpty()) {
      return desc;
    }
    return desc + "\n" + noteText;
  }

  private String extractDescription(String stored) {
    if (stored == null || stored.isBlank()) {
      return null;
    }
    String[] parts = stored.split("\\n", 2);
    return parts[0];
  }

  private String extractNotes(String stored) {
    if (stored == null || stored.isBlank()) {
      return null;
    }
    String[] parts = stored.split("\\n", 2);
    if (parts.length == 2 && !parts[1].isBlank()) {
      return parts[1];
    }
    return null;
  }

  private String buildTyreDescription(Integer tyreCount, String notes) {
    String count = tyreCount == null ? "" : "Count=" + tyreCount;
    String noteText = notes == null ? "" : notes.trim();
    if (count.isEmpty() && noteText.isEmpty()) {
      return null;
    }
    if (count.isEmpty()) {
      return noteText;
    }
    if (noteText.isEmpty()) {
      return count;
    }
    return count + "\n" + noteText;
  }

  private Integer extractTyreCount(String stored) {
    if (stored == null || stored.isBlank()) {
      return null;
    }
    String[] parts = stored.split("\\n", 2);
    if (parts.length > 0 && parts[0].startsWith("Count=")) {
      try {
        return Integer.parseInt(parts[0].substring("Count=".length()));
      } catch (NumberFormatException ignored) {
        return null;
      }
    }
    return null;
  }

  private String extractTyreNotes(String stored) {
    if (stored == null || stored.isBlank()) {
      return null;
    }
    String[] parts = stored.split("\\n", 2);
    if (parts.length > 0 && parts[0].startsWith("Count=")) {
      if (parts.length == 2 && !parts[1].isBlank()) {
        return parts[1];
      }
      return null;
    }
    return stored;
  }

  private BigDecimal normalizeAmount(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private static class DateRange {
    private final LocalDate from;
    private final LocalDate to;

    private DateRange(LocalDate from, LocalDate to) {
      this.from = from;
      this.to = to;
    }
  }
}
