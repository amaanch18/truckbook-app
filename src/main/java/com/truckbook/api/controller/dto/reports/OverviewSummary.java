package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;

public class OverviewSummary {
  private BigDecimal revenueEarned;
  private BigDecimal expensesTotal;
  private BigDecimal profit;
  private BigDecimal cashReceived;
  private BigDecimal outstanding;
  private Long tripCount;

  public OverviewSummary() {}

  public BigDecimal getRevenueEarned() {
    return revenueEarned;
  }

  public void setRevenueEarned(BigDecimal revenueEarned) {
    this.revenueEarned = revenueEarned;
  }

  public BigDecimal getExpensesTotal() {
    return expensesTotal;
  }

  public void setExpensesTotal(BigDecimal expensesTotal) {
    this.expensesTotal = expensesTotal;
  }

  public BigDecimal getProfit() {
    return profit;
  }

  public void setProfit(BigDecimal profit) {
    this.profit = profit;
  }

  public BigDecimal getCashReceived() {
    return cashReceived;
  }

  public void setCashReceived(BigDecimal cashReceived) {
    this.cashReceived = cashReceived;
  }

  public BigDecimal getOutstanding() {
    return outstanding;
  }

  public void setOutstanding(BigDecimal outstanding) {
    this.outstanding = outstanding;
  }

  public Long getTripCount() {
    return tripCount;
  }

  public void setTripCount(Long tripCount) {
    this.tripCount = tripCount;
  }
}
