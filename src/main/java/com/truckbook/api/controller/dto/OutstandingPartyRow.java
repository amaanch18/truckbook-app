package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OutstandingPartyRow {
  private UUID partyId;
  private String partyName;
  private BigDecimal totalFreight;
  private BigDecimal totalPaid;
  private BigDecimal totalOutstanding;
  private BigDecimal partyCredit;
  private BigDecimal netOutstanding;

  public OutstandingPartyRow() {}

  public UUID getPartyId() {
    return partyId;
  }

  public void setPartyId(UUID partyId) {
    this.partyId = partyId;
  }

  public String getPartyName() {
    return partyName;
  }

  public void setPartyName(String partyName) {
    this.partyName = partyName;
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
