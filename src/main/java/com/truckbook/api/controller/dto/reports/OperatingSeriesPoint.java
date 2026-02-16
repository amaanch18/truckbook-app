package com.truckbook.api.controller.dto.reports;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OperatingSeriesPoint {
  private String label;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate dateFrom;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate dateTo;

  private BigDecimal revenueEarned;
  private BigDecimal operatingCost;
  private BigDecimal operatingRatioPct;

  public OperatingSeriesPoint() {}

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
}
