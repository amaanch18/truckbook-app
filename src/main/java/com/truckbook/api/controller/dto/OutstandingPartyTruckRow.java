package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OutstandingPartyTruckRow {
  private UUID truckId;
  private String truckNumber;
  private BigDecimal totalFreight;
  private BigDecimal totalPaid;
  private BigDecimal totalOutstanding;

  public OutstandingPartyTruckRow() {}

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public String getTruckNumber() {
    return truckNumber;
  }

  public void setTruckNumber(String truckNumber) {
    this.truckNumber = truckNumber;
  }

  public BigDecimal getTotalFreight() {
    return totalFreight;
  }

  public void setTotalFreight(BigDecimal totalFreight) {
    this.totalFreight = totalFreight;
  }

  public BigDecimal getTotalPaid() {
    return totalPaid;
  }

  public void setTotalPaid(BigDecimal totalPaid) {
    this.totalPaid = totalPaid;
  }

  public BigDecimal getTotalOutstanding() {
    return totalOutstanding;
  }

  public void setTotalOutstanding(BigDecimal totalOutstanding) {
    this.totalOutstanding = totalOutstanding;
  }
}
