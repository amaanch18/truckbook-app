package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;
import java.util.UUID;

public class TopTruckRow {
  private UUID truckId;
  private String truckNumber;
  private BigDecimal revenueEarned;
  private BigDecimal profit;
  private Long tripCount;

  public TopTruckRow() {}

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

  public BigDecimal getRevenueEarned() {
    return revenueEarned;
  }

  public void setRevenueEarned(BigDecimal revenueEarned) {
    this.revenueEarned = revenueEarned;
  }

  public BigDecimal getProfit() {
    return profit;
  }

  public void setProfit(BigDecimal profit) {
    this.profit = profit;
  }

  public Long getTripCount() {
    return tripCount;
  }

  public void setTripCount(Long tripCount) {
    this.tripCount = tripCount;
  }
}
