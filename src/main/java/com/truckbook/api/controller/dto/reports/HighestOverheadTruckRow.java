package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;
import java.util.UUID;

public class HighestOverheadTruckRow {
  private UUID truckId;
  private String truckNumber;
  private BigDecimal repairs;
  private BigDecimal tyres;
  private BigDecimal overhead;

  public HighestOverheadTruckRow() {}

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
}
