package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OutstandingTruckRow {
  private UUID truckId;
  private String truckNumber;
  private BigDecimal totalFreight;
  private BigDecimal totalPaid;
  private BigDecimal totalOutstanding;
  private BigDecimal partyCredit;
  private BigDecimal netOutstanding;

  public OutstandingTruckRow() {}

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

  public BigDecimal getPartyCredit() {
    return partyCredit;
  }

  public void setPartyCredit(BigDecimal partyCredit) {
    this.partyCredit = partyCredit;
  }

  public BigDecimal getNetOutstanding() {
    return netOutstanding;
  }

  public void setNetOutstanding(BigDecimal netOutstanding) {
    this.netOutstanding = netOutstanding;
  }
}
