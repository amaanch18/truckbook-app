package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;
import java.util.UUID;

public class HighestCostTripRow {
  private UUID tripId;
  private String route;
  private String truckNumber;
  private BigDecimal fuel;
  private BigDecimal tolls;
  private BigDecimal driver;
  private BigDecimal totalTripCost;

  public HighestCostTripRow() {}

  public UUID getTripId() {
    return tripId;
  }

  public void setTripId(UUID tripId) {
    this.tripId = tripId;
  }

  public String getRoute() {
    return route;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public String getTruckNumber() {
    return truckNumber;
  }

  public void setTruckNumber(String truckNumber) {
    this.truckNumber = truckNumber;
  }

  public BigDecimal getFuel() {
    return fuel;
  }

  public void setFuel(BigDecimal fuel) {
    this.fuel = fuel;
  }

  public BigDecimal getTolls() {
    return tolls;
  }

  public void setTolls(BigDecimal tolls) {
    this.tolls = tolls;
  }

  public BigDecimal getDriver() {
    return driver;
  }

  public void setDriver(BigDecimal driver) {
    this.driver = driver;
  }

  public BigDecimal getTotalTripCost() {
    return totalTripCost;
  }

  public void setTotalTripCost(BigDecimal totalTripCost) {
    this.totalTripCost = totalTripCost;
  }
}
