package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class FuelLogResponse {
  private UUID id;
  private UUID tripId;
  private LocalDate filledOn;
  private BigDecimal liters;
  private BigDecimal ratePerLiter;
  private BigDecimal amount;
  private String fuelStation;
  private BigDecimal odometerKm;
  private String notes;
  private OffsetDateTime createdAt;

  public FuelLogResponse() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getTripId() {
    return tripId;
  }

  public void setTripId(UUID tripId) {
    this.tripId = tripId;
  }

  public LocalDate getFilledOn() {
    return filledOn;
  }

  public void setFilledOn(LocalDate filledOn) {
    this.filledOn = filledOn;
  }

  public BigDecimal getLiters() {
    return liters;
  }

  public void setLiters(BigDecimal liters) {
    this.liters = liters;
  }

  public BigDecimal getRatePerLiter() {
    return ratePerLiter;
  }

  public void setRatePerLiter(BigDecimal ratePerLiter) {
    this.ratePerLiter = ratePerLiter;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getFuelStation() {
    return fuelStation;
  }

  public void setFuelStation(String fuelStation) {
    this.fuelStation = fuelStation;
  }

  public BigDecimal getOdometerKm() {
    return odometerKm;
  }

  public void setOdometerKm(BigDecimal odometerKm) {
    this.odometerKm = odometerKm;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
