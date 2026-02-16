package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PartyCreditResponse {
  private UUID partyId;
  private BigDecimal creditAmount;

  public PartyCreditResponse() {}

  public UUID getPartyId() {
    return partyId;
  }

  public void setPartyId(UUID partyId) {
    this.partyId = partyId;
  }

  public BigDecimal getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(BigDecimal creditAmount) {
    this.creditAmount = creditAmount;
  }
}
