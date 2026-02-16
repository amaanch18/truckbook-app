package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TripBreakdownRow {
  private UUID tripId;
  private String tripCode;
  private String fromLocation;
  private String toLocation;
  private UUID truckId;
  private String truckNumber;
  private BigDecimal revenueEarned;
  private BigDecimal fuel;
  private BigDecimal tolls;
  private BigDecimal driver;
  private BigDecimal directProfit;
  private String status;
  private LocalDate startDate;

  public TripBreakdownRow() {}

  public UUID getTripId() {
    return tripId;
  }

  public void setTripId(UUID tripId) {
    this.tripId = tripId;
  }

  public String getTripCode() {
    return tripCode;
  }

  public void setTripCode(String tripCode) {
    this.tripCode = tripCode;
  }

  public String getFromLocation() {
    return fromLocation;
  }

  public void setFromLocation(String fromLocation) {
    this.fromLocation = fromLocation;
  }

  public String getToLocation() {
    return toLocation;
  }

  public void setToLocation(String toLocation) {
    this.toLocation = toLocation;
  }

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public String getTruckNumber() {
    return truckNumber;
  }

  public void setTruckNumber(String truckNumber) {
    this.truckNumber = truckNumber;
  }

  public BigDecimal getRevenueEarned() {
    return revenueEarned;
  }

  public void setRevenueEarned(BigDecimal revenueEarned) {
    this.revenueEarned = revenueEarned;
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

  public BigDecimal getDirectProfit() {
    return directProfit;
  }

  public void setDirectProfit(BigDecimal directProfit) {
    this.directProfit = directProfit;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }
}
