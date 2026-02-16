package com.truckbook.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FuelLogCreateRequest {
  @NotNull
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate filledOn;

  @NotNull
  @Positive
  private BigDecimal liters;

  @NotNull
  @PositiveOrZero
  private BigDecimal ratePerLiter;

  private String fuelStation;
  private Integer odometerKm;
  private String notes;

  public FuelLogCreateRequest() {}

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

  public String getFuelStation() {
    return fuelStation;
  }

  public void setFuelStation(String fuelStation) {
    this.fuelStation = fuelStation;
  }

  public Integer getOdometerKm() {
    return odometerKm;
  }

  public void setOdometerKm(Integer odometerKm) {
    this.odometerKm = odometerKm;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
