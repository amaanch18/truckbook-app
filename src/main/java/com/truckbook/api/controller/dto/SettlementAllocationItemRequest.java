package com.truckbook.api.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public class SettlementAllocationItemRequest {
  @NotNull
  private UUID tripId;

  @NotNull
  @Positive
  private BigDecimal amountApplied;

  public SettlementAllocationItemRequest() {}

  public UUID getTripId() {
    return tripId;
  }

  public void setTripId(UUID tripId) {
    this.tripId = tripId;
  }

  public BigDecimal getAmountApplied() {
    return amountApplied;
  }

  public void setAmountApplied(BigDecimal amountApplied) {
    this.amountApplied = amountApplied;
  }
}
