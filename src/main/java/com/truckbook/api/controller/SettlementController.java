package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.OutstandingTripRow;
import com.truckbook.api.controller.dto.OutstandingPartyTruckRow;
import com.truckbook.api.controller.dto.SettlementAllocateRequest;
import com.truckbook.api.controller.dto.SettlementCreateRequest;
import com.truckbook.api.controller.dto.SettlementDetailResponse;
import com.truckbook.api.controller.dto.SettlementResponse;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.SettlementService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {
  private final SettlementService settlementService;

  public SettlementController(SettlementService settlementService) {
    this.settlementService = settlementService;
  }

  @PostMapping
  public SettlementResponse create(@Valid @RequestBody SettlementCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.create(orgId, request);
  }

  @GetMapping
  public List<SettlementResponse> list() {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.list(orgId);
  }

  @GetMapping("/{id}")
  public SettlementDetailResponse get(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.get(orgId, id);
  }

  @PostMapping("/{id}/allocations")
  public SettlementDetailResponse allocate(
      @PathVariable UUID id,
      @Valid @RequestBody SettlementAllocateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.allocate(orgId, id, request);
  }

  @GetMapping("/outstanding")
  public List<?> outstanding(@RequestParam String mode) {
    UUID orgId = OrgContext.requireOrgId();
    if ("party".equalsIgnoreCase(mode)) {
      return settlementService.outstandingByParty(orgId);
    }
    if ("truck".equalsIgnoreCase(mode)) {
      return settlementService.outstandingByTruck(orgId);
    }
    throw new BadRequestException("mode must be party or truck");
  }

  @GetMapping("/outstanding/party/{partyId}")
  public List<OutstandingTripRow> outstandingByParty(@PathVariable UUID partyId) {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.outstandingTripsByParty(orgId, partyId);
  }

  @GetMapping("/outstanding/party/{partyId}/trucks")
  public List<OutstandingPartyTruckRow> outstandingTrucksByParty(@PathVariable UUID partyId) {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.outstandingTrucksByParty(orgId, partyId);
  }

  @GetMapping("/outstanding/truck/{truckId}")
  public List<OutstandingTripRow> outstandingByTruck(@PathVariable UUID truckId) {
    UUID orgId = OrgContext.requireOrgId();
    return settlementService.outstandingTripsByTruck(orgId, truckId);
  }
}
