package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;

public class OperatingSummary {
  private BigDecimal revenueEarned;
  private BigDecimal operatingCost;
  private BigDecimal operatingRatioPct;
  private Long tripCount;

  public OperatingSummary() {}

  public BigDecimal getRevenueEarned() {
    return revenueEarned;
  }

  public void setRevenueEarned(BigDecimal revenueEarned) {
    this.revenueEarned = revenueEarned;
  }

  public BigDecimal getOperatingCost() {
    return operatingCost;
  }

  public void setOperatingCost(BigDecimal operatingCost) {
    this.operatingCost = operatingCost;
  }

  public BigDecimal getOperatingRatioPct() {
    return operatingRatioPct;
  }

  public void setOperatingRatioPct(BigDecimal operatingRatioPct) {
    this.operatingRatioPct = operatingRatioPct;
  }

  public Long getTripCount() {
    return tripCount;
  }

  public void setTripCount(Long tripCount) {
    this.tripCount = tripCount;
  }
}
