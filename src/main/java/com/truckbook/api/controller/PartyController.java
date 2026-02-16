package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.PartyCreateRequest;
import com.truckbook.api.controller.dto.PartyCreditResponse;
import com.truckbook.api.controller.dto.PartyResponse;
import com.truckbook.api.controller.dto.PartyUpdateRequest;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.PartyService;
import com.truckbook.api.service.SettlementService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parties")
public class PartyController {
  private final PartyService partyService;
  private final SettlementService settlementService;

  public PartyController(PartyService partyService, SettlementService settlementService) {
    this.partyService = partyService;
    this.settlementService = settlementService;
  }

  @PostMapping
  public PartyResponse create(@Valid @RequestBody PartyCreateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return partyService.create(orgId, request);
  }

  @GetMapping
  public List<PartyResponse> list(@RequestParam(required = false) String q) {
    UUID orgId = OrgContext.requireOrgId();
    return partyService.list(orgId, q);
  }

  @GetMapping("/{id}")
  public PartyResponse get(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return partyService.get(orgId, id);
  }

  @PutMapping("/{id}")
  public PartyResponse update(
      @PathVariable UUID id,
      @Valid @RequestBody PartyUpdateRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    return partyService.update(orgId, id, request);
  }

  @DeleteMapping("/{id}")
  public Map<String, String> delete(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    return Map.of("message", partyService.delete(orgId, id));
  }

  @GetMapping("/{id}/credit")
  public PartyCreditResponse credit(@PathVariable UUID id) {
    UUID orgId = OrgContext.requireOrgId();
    PartyCreditResponse response = new PartyCreditResponse();
    response.setPartyId(id);
    response.setCreditAmount(settlementService.partyCredit(orgId, id));
    return response;
  }
}
