package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.truckcosts.RepairCreateRequest;
import com.truckbook.api.controller.dto.truckcosts.RepairResponse;
import com.truckbook.api.controller.dto.truckcosts.RepairUpdateRequest;
import com.truckbook.api.controller.dto.truckcosts.TruckCostSummaryResponse;
import com.truckbook.api.controller.dto.truckcosts.TyreCreateRequest;
import com.truckbook.api.controller.dto.truckcosts.TyreResponse;
import com.truckbook.api.controller.dto.truckcosts.TyreUpdateRequest;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.TruckCostService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trucks/{truckId}")
public class TruckCostController {
  private final TruckCostService truckCostService;

  public TruckCostController(TruckCostService truckCostService) {
    this.truckCostService = truckCostService;
  }

  @PostMapping("/repairs")
  public RepairResponse createRepair(
      @PathVariable UUID truckId,
      @Valid @RequestBody RepairCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.createRepair(orgId, truckId, request);
  }

  @GetMapping("/repairs")
  public List<RepairResponse> listRepairs(
      @PathVariable UUID truckId,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.listRepairs(orgId, truckId, from, to);
  }

  @PutMapping("/repairs/{repairId}")
  public RepairResponse updateRepair(
      @PathVariable UUID truckId,
      @PathVariable UUID repairId,
      @Valid @RequestBody RepairUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.updateRepair(orgId, truckId, repairId, request);
  }

  @DeleteMapping("/repairs/{repairId}")
  public Map<String, String> deleteRepair(
      @PathVariable UUID truckId,
      @PathVariable UUID repairId) {
    UUID orgId = OrgContext.requireOrgId();
    truckCostService.deleteRepair(orgId, truckId, repairId);
    return Map.of("message", "Repair deleted successfully");
  }

  @PostMapping("/tyres")
  public TyreResponse createTyre(
      @PathVariable UUID truckId,
      @Valid @RequestBody TyreCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.createTyre(orgId, truckId, request);
  }

  @GetMapping("/tyres")
  public List<TyreResponse> listTyres(
      @PathVariable UUID truckId,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.listTyres(orgId, truckId, from, to);
  }

  @PutMapping("/tyres/{tyreId}")
  public TyreResponse updateTyre(
      @PathVariable UUID truckId,
      @PathVariable UUID tyreId,
      @Valid @RequestBody TyreUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.updateTyre(orgId, truckId, tyreId, request);
  }

  @DeleteMapping("/tyres/{tyreId}")
  public Map<String, String> deleteTyre(
      @PathVariable UUID truckId,
      @PathVariable UUID tyreId) {
    UUID orgId = OrgContext.requireOrgId();
    truckCostService.deleteTyre(orgId, truckId, tyreId);
    return Map.of("message", "Tyre expense deleted successfully");
  }

  @GetMapping("/costs/summary")
  public TruckCostSummaryResponse summary(
      @PathVariable UUID truckId,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to) {
    UUID orgId = OrgContext.requireOrgId();
    return truckCostService.summary(orgId, truckId, from, to);
  }
}
