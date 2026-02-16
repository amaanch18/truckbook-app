package com.truckbook.api.controller.dto;

public class CountsDto {
  private long trucks;
  private long trips;

  public CountsDto() {}

  public long getTrucks() {
    return trucks;
  }

  public void setTrucks(long trucks) {
    this.trucks = trucks;
  }

  public long getTrips() {
    return trips;
  }

  public void setTrips(long trips) {
    this.trips = trips;
  }
}
