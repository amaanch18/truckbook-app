package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.TruckCreateRequest;
import com.truckbook.api.controller.dto.TruckResponse;
import com.truckbook.api.controller.dto.TruckUpdateRequest;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.TruckService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/trucks")
public class TruckController {
  private final TruckService truckService;

  public TruckController(TruckService truckService) {
    this.truckService = truckService;
  }

  @PostMapping
  public TruckResponse create(@Valid @RequestBody TruckCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return truckService.create(orgId, request);
  }

  @GetMapping
  public List<TruckResponse> list() {
    UUID orgId = OrgContext.requireOrgId();
    return truckService.list(orgId);
  }

  @GetMapping("/{id}")
  public TruckResponse get(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return truckService.get(orgId, id);
  }

  @PutMapping("/{id}")
  public TruckResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody TruckUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return truckService.update(orgId, id, request);
  }

  @DeleteMapping("/{id}")
  public java.util.Map<String, String> delete(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return java.util.Map.of("message", truckService.delete(orgId, id));
  }
}
