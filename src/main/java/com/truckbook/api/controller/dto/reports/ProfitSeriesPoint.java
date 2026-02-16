package com.truckbook.api.controller.dto.reports;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProfitSeriesPoint {
  private String label;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate dateFrom;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate dateTo;

  private BigDecimal revenueEarned;
  private BigDecimal expensesTotal;
  private BigDecimal profit;
  private BigDecimal marginPct;

  public ProfitSeriesPoint() {}

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public LocalDate getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(LocalDate dateFrom) {
    this.dateFrom = dateFrom;
  }

  public LocalDate getDateTo() {
    return dateTo;
  }

  public void setDateTo(LocalDate dateTo) {
    this.dateTo = dateTo;
  }

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
}
