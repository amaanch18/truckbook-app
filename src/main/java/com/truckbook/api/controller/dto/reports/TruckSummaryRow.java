package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;
import java.util.UUID;

public class TruckSummaryRow {
  private UUID truckId;
  private String truckNumber;
  private long trips;
  private BigDecimal revenueEarned;
  private BigDecimal tripCosts;
  private BigDecimal directProfit;
  private BigDecimal repairs;
  private BigDecimal tyres;
  private BigDecimal overhead;
  private BigDecimal netProfit;

  public TruckSummaryRow() {}

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

  public long getTrips() {
    return trips;
  }

  public void setTrips(long trips) {
    this.trips = trips;
  }

  public BigDecimal getRevenueEarned() {
    return revenueEarned;
  }

  public void setRevenueEarned(BigDecimal revenueEarned) {
    this.revenueEarned = revenueEarned;
  }

  public BigDecimal getTripCosts() {
    return tripCosts;
  }

  public void setTripCosts(BigDecimal tripCosts) {
    this.tripCosts = tripCosts;
  }

  public BigDecimal getDirectProfit() {
    return directProfit;
  }

  public void setDirectProfit(BigDecimal directProfit) {
    this.directProfit = directProfit;
  }

  public BigDecimal getRepairs() {
    return repairs;
  }

  public void setRepairs(BigDecimal repairs) {
    this.repairs = repairs;
  }

  public BigDecimal getTyres() {
    return tyres;
  }

  public void setTyres(BigDecimal tyres) {
    this.tyres = tyres;
  }

  public BigDecimal getOverhead() {
    return overhead;
  }

  public void setOverhead(BigDecimal overhead) {
    this.overhead = overhead;
  }

  public BigDecimal getNetProfit() {
    return netProfit;
  }

  public void setNetProfit(BigDecimal netProfit) {
    this.netProfit = netProfit;
  }
}
