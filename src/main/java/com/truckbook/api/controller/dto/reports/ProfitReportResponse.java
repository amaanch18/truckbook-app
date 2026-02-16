package com.truckbook.api.controller.dto.reports;

import java.util.List;

public class ProfitReportResponse {
  private ReportRange range;
  private ProfitSummary summary;
  private List<ProfitSeriesPoint> series;
  private ExpenseBreakdown expenseBreakdown;
  private List<TripBreakdownRow> tripBreakdown;
  private List<TruckSummaryRow> truckSummary;

  public ProfitReportResponse() {}

  public ReportRange getRange() {
    return range;
  }

  public void setRange(ReportRange range) {
    this.range = range;
  }

  public ProfitSummary getSummary() {
    return summary;
  }

  public void setSummary(ProfitSummary summary) {
    this.summary = summary;
  }

  public List<ProfitSeriesPoint> getSeries() {
    return series;
  }

  public void setSeries(List<ProfitSeriesPoint> series) {
    this.series = series;
  }

  public ExpenseBreakdown getExpenseBreakdown() {
    return expenseBreakdown;
  }

  public void setExpenseBreakdown(ExpenseBreakdown expenseBreakdown) {
    this.expenseBreakdown = expenseBreakdown;
  }

  public List<TripBreakdownRow> getTripBreakdown() {
    return tripBreakdown;
  }

  public void setTripBreakdown(List<TripBreakdownRow> tripBreakdown) {
    this.tripBreakdown = tripBreakdown;
  }

  public List<TruckSummaryRow> getTruckSummary() {
    return truckSummary;
  }

  public void setTruckSummary(List<TruckSummaryRow> truckSummary) {
    this.truckSummary = truckSummary;
  }
}
