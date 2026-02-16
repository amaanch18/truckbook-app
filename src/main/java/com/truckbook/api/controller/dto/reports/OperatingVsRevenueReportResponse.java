package com.truckbook.api.controller.dto.reports;

import java.util.List;

public class OperatingVsRevenueReportResponse {
  private ReportRange range;
  private OperatingSummary summary;
  private List<OperatingSeriesPoint> series;
  private List<HighestCostTripRow> highestCostTrips;
  private List<HighestOverheadTruckRow> highestOverheadTrucks;

  public OperatingVsRevenueReportResponse() {}

  public ReportRange getRange() {
    return range;
  }

  public void setRange(ReportRange range) {
    this.range = range;
  }

  public OperatingSummary getSummary() {
    return summary;
  }

  public void setSummary(OperatingSummary summary) {
    this.summary = summary;
  }

  public List<OperatingSeriesPoint> getSeries() {
    return series;
  }

  public void setSeries(List<OperatingSeriesPoint> series) {
    this.series = series;
  }

  public List<HighestCostTripRow> getHighestCostTrips() {
    return highestCostTrips;
  }

  public void setHighestCostTrips(List<HighestCostTripRow> highestCostTrips) {
    this.highestCostTrips = highestCostTrips;
  }

  public List<HighestOverheadTruckRow> getHighestOverheadTrucks() {
    return highestOverheadTrucks;
  }

  public void setHighestOverheadTrucks(List<HighestOverheadTruckRow> highestOverheadTrucks) {
    this.highestOverheadTrucks = highestOverheadTrucks;
  }
}
