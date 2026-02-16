package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class SettlementAllocationResponse {
  private UUID id;
  private UUID settlementId;
  private UUID tripId;
  private BigDecimal amountApplied;
  private BigDecimal pendingAmount;
  private OffsetDateTime createdAt;

  public SettlementAllocationResponse() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getSettlementId() {
    return settlementId;
  }

  public void setSettlementId(UUID settlementId) {
    this.settlementId = settlementId;
  }

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

  public BigDecimal getPendingAmount() {
    return pendingAmount;
  }

  public void setPendingAmount(BigDecimal pendingAmount) {
    this.pendingAmount = pendingAmount;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
