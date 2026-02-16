package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;
import java.util.UUID;

public class TopPartyRow {
  private UUID partyId;
  private String partyName;
  private BigDecimal revenueEarned;
  private BigDecimal outstanding;

  public TopPartyRow() {}

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

  public BigDecimal getRevenueEarned() {
    return revenueEarned;
  }

  public void setRevenueEarned(BigDecimal revenueEarned) {
    this.revenueEarned = revenueEarned;
  }

  public BigDecimal getOutstanding() {
    return outstanding;
  }

  public void setOutstanding(BigDecimal outstanding) {
    this.outstanding = outstanding;
  }
}
