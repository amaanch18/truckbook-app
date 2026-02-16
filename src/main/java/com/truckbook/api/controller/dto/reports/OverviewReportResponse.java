package com.truckbook.api.controller.dto.reports;

import java.util.List;

public class OverviewReportResponse {
  private ReportRange range;
  private OverviewSummary summary;
  private List<OverviewSeriesPoint> series;
  private List<TopPartyRow> topParties;
  private List<TopTruckRow> topTrucks;

  public OverviewReportResponse() {}

  public ReportRange getRange() {
    return range;
  }

  public void setRange(ReportRange range) {
    this.range = range;
  }

  public OverviewSummary getSummary() {
    return summary;
  }

  public void setSummary(OverviewSummary summary) {
    this.summary = summary;
  }

  public List<OverviewSeriesPoint> getSeries() {
    return series;
  }

  public void setSeries(List<OverviewSeriesPoint> series) {
    this.series = series;
  }

  public List<TopPartyRow> getTopParties() {
    return topParties;
  }

  public void setTopParties(List<TopPartyRow> topParties) {
    this.topParties = topParties;
  }

  public List<TopTruckRow> getTopTrucks() {
    return topTrucks;
  }

  public void setTopTrucks(List<TopTruckRow> topTrucks) {
    this.topTrucks = topTrucks;
  }
}
