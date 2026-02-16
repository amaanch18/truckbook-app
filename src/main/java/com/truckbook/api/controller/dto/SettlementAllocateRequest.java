package com.truckbook.api.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class SettlementAllocateRequest {
  @NotEmpty
  @Valid
  private List<SettlementAllocationItemRequest> allocations;

  public SettlementAllocateRequest() {}

  public List<SettlementAllocationItemRequest> getAllocations() {
    return allocations;
  }

  public void setAllocations(List<SettlementAllocationItemRequest> allocations) {
    this.allocations = allocations;
  }
}
