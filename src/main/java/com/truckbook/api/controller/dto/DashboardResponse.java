package com.truckbook.api.controller.dto;

import java.util.List;

public class DashboardResponse {
  private CountsDto counts;
  private PendingSettlementDto pendingSettlement;
  private List<RecentTripDto> recentTrips;

  public DashboardResponse() {}

  public CountsDto getCounts() {
    return counts;
  }

  public void setCounts(CountsDto counts) {
    this.counts = counts;
  }

  public PendingSettlementDto getPendingSettlement() {
    return pendingSettlement;
  }

  public void setPendingSettlement(PendingSettlementDto pendingSettlement) {
    this.pendingSettlement = pendingSettlement;
  }

  public List<RecentTripDto> getRecentTrips() {
    return recentTrips;
  }

  public void setRecentTrips(List<RecentTripDto> recentTrips) {
    this.recentTrips = recentTrips;
  }
}
