package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.TruckCreateRequest;
import com.truckbook.api.controller.dto.TruckComplianceItemRequest;
import com.truckbook.api.controller.dto.TruckComplianceItemResponse;
import com.truckbook.api.controller.dto.TruckComplianceRequest;
import com.truckbook.api.controller.dto.TruckComplianceResponse;
import com.truckbook.api.controller.dto.TruckResponse;
import com.truckbook.api.controller.dto.TruckUpdateRequest;
import com.truckbook.api.entity.Truck;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.exception.ConflictException;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TruckRepository;
import com.truckbook.api.service.TruckService;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TruckServiceImpl implements TruckService {
  private static final Set<String> ALLOWED_TRUCK_TYPES =
      Set.of("OPEN", "CONTAINER", "TRAILER", "TIPPER", "TANKER", "OTHER");
  private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "INACTIVE");
  private static final Set<String> ALLOWED_COMPLIANCE_STATUSES = Set.of("VALID", "EXPIRED", "MISSING");

  private final TruckRepository truckRepository;
  private final TripRepository tripRepository;

  public TruckServiceImpl(TruckRepository truckRepository, TripRepository tripRepository) {
    this.truckRepository = truckRepository;
    this.tripRepository = tripRepository;
  }

  @Override
  public TruckResponse create(UUID orgId, TruckCreateRequest request) {
    Map<String, String> errors = new LinkedHashMap<>();
    String status = normalizeStatusForCreate(request.getStatus(), errors);
    String truckType = normalizeTruckType(request.getTruckType(), errors, "truckType");

    truckRepository.findByOrgIdAndTruckNumber(orgId, request.getTruckNumber())
        .ifPresent(existing -> {
          throw new ConflictException("Truck number already exists for this organization");
        });
    OffsetDateTime now = OffsetDateTime.now();
    Truck truck = new Truck();
    truck.setId(UUID.randomUUID());
    truck.setOrgId(orgId);
    truck.setTruckNumber(request.getTruckNumber());
    truck.setStatus(status);
    truck.setNotes(request.getNotes());
    truck.setTruckType(truckType);
    truck.setCreatedAt(now);
    truck.setUpdatedAt(now);

    applyComplianceCreate(request.getCompliance(), truck, errors);

    if (!errors.isEmpty()) {
      throw new BadRequestException("Validation failed", errors);
    }

    return toResponse(truckRepository.save(truck));
  }

  @Override
  public List<TruckResponse> list(UUID orgId) {
    return truckRepository.findAllByOrgId(orgId).stream().map(this::toResponse).toList();
  }

  @Override
  public TruckResponse get(UUID orgId, UUID id) {
    Truck truck = truckRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Truck not found"));
    return toResponse(truck);
  }

  @Override
  public TruckResponse update(UUID orgId, UUID id, TruckUpdateRequest request) {
    Truck truck = truckRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Truck not found"));
    Map<String, String> errors = new LinkedHashMap<>();

    if (request.getTruckNumber() != null && !request.getTruckNumber().isBlank()) {
      truckRepository.findByOrgIdAndTruckNumber(orgId, request.getTruckNumber())
          .ifPresent(existing -> {
            if (!existing.getId().equals(truck.getId())) {
              throw new ConflictException("Truck number already exists for this organization");
            }
          });
      truck.setTruckNumber(request.getTruckNumber());
    }
    if (request.getStatus() != null && !request.getStatus().isBlank()) {
      String normalizedStatus = normalizeStatusOptional(request.getStatus(), errors);
      if (normalizedStatus != null) {
        truck.setStatus(normalizedStatus);
      }
    }
    if (request.getNotes() != null) {
      truck.setNotes(request.getNotes());
    }
    if (request.getTruckType() != null) {
      String normalizedType = normalizeTruckType(request.getTruckType(), errors, "truckType");
      if (normalizedType != null || request.getTruckType().isBlank()) {
        truck.setTruckType(normalizedType);
      }
    }
    if (request.getCompliance() != null) {
      applyComplianceUpdate(request.getCompliance(), truck, errors);
    }

    if (!errors.isEmpty()) {
      throw new BadRequestException("Validation failed", errors);
    }

    truck.setUpdatedAt(OffsetDateTime.now());
    return toResponse(truckRepository.save(truck));
  }

  @Override
  public String delete(UUID orgId, UUID id) {
    Truck truck = truckRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Truck not found"));

    if (tripRepository.existsByOrgIdAndTruckId(orgId, id)) {
      throw new ConflictException("Cannot delete truck with active trips");
    }

    truckRepository.delete(truck);
    return "Truck deleted successfully";
  }

  private TruckResponse toResponse(Truck truck) {
    TruckResponse response = new TruckResponse();
    response.setId(truck.getId());
    response.setOrgId(truck.getOrgId());
    response.setTruckNumber(truck.getTruckNumber());
    response.setTruckType(truck.getTruckType());
    response.setStatus(truck.getStatus());
    response.setNotes(truck.getNotes());
    response.setCompliance(toComplianceResponse(truck));
    response.setCreatedAt(truck.getCreatedAt());
    response.setUpdatedAt(truck.getUpdatedAt());
    return response;
  }

  private TruckComplianceResponse toComplianceResponse(Truck truck) {
    TruckComplianceResponse compliance = new TruckComplianceResponse();
    TruckComplianceItemResponse insurance = new TruckComplianceItemResponse();
    insurance.setStatus(truck.getInsuranceStatus());
    insurance.setExpiryDate(truck.getInsuranceExpiry());
    compliance.setInsurance(insurance);

    TruckComplianceItemResponse permit = new TruckComplianceItemResponse();
    permit.setStatus(truck.getPermitStatus());
    permit.setExpiryDate(truck.getPermitExpiry());
    compliance.setPermit(permit);

    TruckComplianceItemResponse fitness = new TruckComplianceItemResponse();
    fitness.setStatus(truck.getFitnessStatus());
    fitness.setExpiryDate(truck.getFitnessExpiry());
    compliance.setFitness(fitness);

    return compliance;
  }

  private String normalizeStatusForCreate(String status, Map<String, String> errors) {
    if (status == null || status.isBlank()) {
      errors.put("status", "must not be blank");
      return null;
    }
    String normalized = status.toUpperCase();
    if (!ALLOWED_STATUSES.contains(normalized)) {
      errors.put("status", "Status must be ACTIVE or INACTIVE");
      return null;
    }
    return normalized;
  }

  private String normalizeStatusOptional(String status, Map<String, String> errors) {
    if (status == null || status.isBlank()) {
      return null;
    }
    String normalized = status.toUpperCase();
    if (!ALLOWED_STATUSES.contains(normalized)) {
      errors.put("status", "Status must be ACTIVE or INACTIVE");
      return null;
    }
    return normalized;
  }

  private String normalizeTruckType(String truckType, Map<String, String> errors, String field) {
    if (truckType == null) {
      return null;
    }
    String trimmed = truckType.trim();
    if (trimmed.isEmpty()) {
      return null;
    }
    String normalized = trimmed.toUpperCase();
    if (!ALLOWED_TRUCK_TYPES.contains(normalized)) {
      errors.put(field, "truckType must be one of OPEN, CONTAINER, TRAILER, TIPPER, TANKER, OTHER");
      return null;
    }
    return normalized;
  }

  private void applyComplianceCreate(
      TruckComplianceRequest compliance,
      Truck truck,
      Map<String, String> errors) {
    if (compliance == null) {
      return;
    }
    if (compliance.getInsurance() != null) {
      applyComplianceItemCreate(
          compliance.getInsurance(),
          "compliance.insurance",
          errors,
          truck::setInsuranceStatus,
          truck::setInsuranceExpiry);
    }
    if (compliance.getPermit() != null) {
      applyComplianceItemCreate(
          compliance.getPermit(),
          "compliance.permit",
          errors,
          truck::setPermitStatus,
          truck::setPermitExpiry);
    }
    if (compliance.getFitness() != null) {
      applyComplianceItemCreate(
          compliance.getFitness(),
          "compliance.fitness",
          errors,
          truck::setFitnessStatus,
          truck::setFitnessExpiry);
    }
  }

  private void applyComplianceUpdate(
      TruckComplianceRequest compliance,
      Truck truck,
      Map<String, String> errors) {
    if (compliance == null) {
      return;
    }
    if (compliance.getInsurance() != null) {
      applyComplianceItemUpdate(
          compliance.getInsurance(),
          "compliance.insurance",
          errors,
          truck::setInsuranceStatus,
          truck::setInsuranceExpiry);
    }
    if (compliance.getPermit() != null) {
      applyComplianceItemUpdate(
          compliance.getPermit(),
          "compliance.permit",
          errors,
          truck::setPermitStatus,
          truck::setPermitExpiry);
    }
    if (compliance.getFitness() != null) {
      applyComplianceItemUpdate(
          compliance.getFitness(),
          "compliance.fitness",
          errors,
          truck::setFitnessStatus,
          truck::setFitnessExpiry);
    }
  }

  private void applyComplianceItemCreate(
      TruckComplianceItemRequest item,
      String fieldPrefix,
      Map<String, String> errors,
      java.util.function.Consumer<String> statusSetter,
      java.util.function.Consumer<java.time.LocalDate> expirySetter) {
    if (item.getStatus() != null && !item.getStatus().isBlank()) {
      String normalized = item.getStatus().toUpperCase();
      if (!ALLOWED_COMPLIANCE_STATUSES.contains(normalized)) {
        errors.put(fieldPrefix + ".status", "Status must be VALID, EXPIRED, or MISSING");
      } else {
        statusSetter.accept(normalized);
      }
    }
    if (item.getExpiryDate() != null) {
      expirySetter.accept(item.getExpiryDate());
    }
  }

  private void applyComplianceItemUpdate(
      TruckComplianceItemRequest item,
      String fieldPrefix,
      Map<String, String> errors,
      java.util.function.Consumer<String> statusSetter,
      java.util.function.Consumer<java.time.LocalDate> expirySetter) {
    if (item.getStatus() != null) {
      if (item.getStatus().isBlank()) {
        statusSetter.accept(null);
      } else {
        String normalized = item.getStatus().toUpperCase();
        if (!ALLOWED_COMPLIANCE_STATUSES.contains(normalized)) {
          errors.put(fieldPrefix + ".status", "Status must be VALID, EXPIRED, or MISSING");
        } else {
          statusSetter.accept(normalized);
        }
      }
    }
    if (item.getExpiryDate() != null) {
      expirySetter.accept(item.getExpiryDate());
    }
  }
}
