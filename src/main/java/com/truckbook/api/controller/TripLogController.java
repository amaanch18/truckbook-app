package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.DriverExpenseCreateRequest;
import com.truckbook.api.controller.dto.DriverExpenseResponse;
import com.truckbook.api.controller.dto.DriverExpenseUpdateRequest;
import com.truckbook.api.controller.dto.FuelLogCreateRequest;
import com.truckbook.api.controller.dto.FuelLogResponse;
import com.truckbook.api.controller.dto.FuelLogUpdateRequest;
import com.truckbook.api.controller.dto.TollCreateRequest;
import com.truckbook.api.controller.dto.TollResponse;
import com.truckbook.api.controller.dto.TollUpdateRequest;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.TripLogService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips/{tripId}")
public class TripLogController {
  private final TripLogService tripLogService;

  public TripLogController(TripLogService tripLogService) {
    this.tripLogService = tripLogService;
  }

  @PostMapping("/fuel")
  public FuelLogResponse createFuel(
      @PathVariable UUID tripId,
      @Valid @RequestBody FuelLogCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.createFuel(orgId, tripId, request);
  }

  @GetMapping("/fuel")
  public List<FuelLogResponse> listFuel(@PathVariable UUID tripId) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.listFuel(orgId, tripId);
  }

  @PutMapping("/fuel/{fuelId}")
  public FuelLogResponse updateFuel(
      @PathVariable UUID tripId,
      @PathVariable UUID fuelId,
      @Valid @RequestBody FuelLogUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.updateFuel(orgId, tripId, fuelId, request);
  }

  @DeleteMapping("/fuel/{fuelId}")
  public Map<String, String> deleteFuel(
      @PathVariable UUID tripId,
      @PathVariable UUID fuelId) {
    UUID orgId = OrgContext.requireOrgId();
    tripLogService.deleteFuel(orgId, tripId, fuelId);
    return Map.of("message", "Fuel log deleted successfully");
  }

  @PostMapping("/tolls")
  public TollResponse createToll(
      @PathVariable UUID tripId,
      @Valid @RequestBody TollCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.createToll(orgId, tripId, request);
  }

  @GetMapping("/tolls")
  public List<TollResponse> listTolls(@PathVariable UUID tripId) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.listTolls(orgId, tripId);
  }

  @DeleteMapping("/tolls/{tollId}")
  public Map<String, String> deleteToll(
      @PathVariable UUID tripId,
      @PathVariable UUID tollId) {
    UUID orgId = OrgContext.requireOrgId();
    tripLogService.deleteToll(orgId, tripId, tollId);
    return Map.of("message", "Toll log deleted successfully");
  }

  @PutMapping("/tolls/{tollId}")
  public TollResponse updateToll(
      @PathVariable UUID tripId,
      @PathVariable UUID tollId,
      @Valid @RequestBody TollUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.updateToll(orgId, tripId, tollId, request);
  }

  @PostMapping("/driver-expenses")
  public DriverExpenseResponse createDriverExpense(
      @PathVariable UUID tripId,
      @Valid @RequestBody DriverExpenseCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.createDriverExpense(orgId, tripId, request);
  }

  @GetMapping("/driver-expenses")
  public List<DriverExpenseResponse> listDriverExpenses(@PathVariable UUID tripId) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.listDriverExpenses(orgId, tripId);
  }

  @DeleteMapping("/driver-expenses/{expenseId}")
  public Map<String, String> deleteDriverExpense(
      @PathVariable UUID tripId,
      @PathVariable UUID expenseId) {
    UUID orgId = OrgContext.requireOrgId();
    tripLogService.deleteDriverExpense(orgId, tripId, expenseId);
    return Map.of("message", "Driver expense deleted successfully");
  }

  @PutMapping("/driver-expenses/{expenseId}")
  public DriverExpenseResponse updateDriverExpense(
      @PathVariable UUID tripId,
      @PathVariable UUID expenseId,
      @Valid @RequestBody DriverExpenseUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripLogService.updateDriverExpense(orgId, tripId, expenseId, request);
  }
}
