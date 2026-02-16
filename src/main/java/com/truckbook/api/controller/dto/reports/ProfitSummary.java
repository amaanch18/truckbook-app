package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;

public class ProfitSummary {
  private BigDecimal revenueEarned;
  private BigDecimal expensesTotal;
  private BigDecimal profit;
  private BigDecimal marginPct;
  private Long tripCount;

  public ProfitSummary() {}

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

  public BigDecimal getMarginPct() {
    return marginPct;
  }

  public void setMarginPct(BigDecimal marginPct) {
    this.marginPct = marginPct;
  }

  public Long getTripCount() {
    return tripCount;
  }

  public void setTripCount(Long tripCount) {
    this.tripCount = tripCount;
  }
}
