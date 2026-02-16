package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.TripCreateRequest;
import com.truckbook.api.controller.dto.TripResponse;
import com.truckbook.api.controller.dto.TripUpdateRequest;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.TripService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")
public class TripController {
  private final TripService tripService;

  public TripController(TripService tripService) {
    this.tripService = tripService;
  }

  @PostMapping
  public TripResponse create(@Valid @RequestBody TripCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripService.create(orgId, request);
  }

  @GetMapping
  public List<TripResponse> list(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) UUID truckId,
      @RequestParam(required = false) UUID partyId,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dateFrom,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate dateTo) {
    UUID orgId = OrgContext.requireOrgId();
    return tripService.list(orgId, status, truckId, partyId, dateFrom, dateTo);
  }

  @GetMapping("/{id}")
  public TripResponse get(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return tripService.get(orgId, id);
  }

  @PutMapping("/{id}")
  public TripResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody TripUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return tripService.update(orgId, id, request);
  }

  @PatchMapping("/{id}/complete")
  public TripResponse complete(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return tripService.complete(orgId, id);
  }

  @DeleteMapping("/{id}")
  public java.util.Map<String, String> delete(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    String message = tripService.delete(orgId, id);
    return java.util.Map.of("message", message);
  }
}
