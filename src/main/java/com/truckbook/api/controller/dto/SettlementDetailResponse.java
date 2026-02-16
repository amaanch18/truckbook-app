package com.truckbook.api.controller.dto;

import java.util.List;

public class SettlementDetailResponse {
  private SettlementResponse settlement;
  private List<SettlementAllocationResponse> allocations;

  public SettlementDetailResponse() {}

  public SettlementDetailResponse(
      SettlementResponse settlement,
      List<SettlementAllocationResponse> allocations) {
    this.settlement = settlement;
    this.allocations = allocations;
  }

  public SettlementResponse getSettlement() {
    return settlement;
  }

  public void setSettlement(SettlementResponse settlement) {
    this.settlement = settlement;
  }

  public List<SettlementAllocationResponse> getAllocations() {
    return allocations;
  }

  public void setAllocations(List<SettlementAllocationResponse> allocations) {
    this.allocations = allocations;
  }
}
