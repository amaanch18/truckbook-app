package com.truckbook.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TripCreateRequest {
  @NotNull
  private UUID truckId;

  private UUID partyId;

  @NotBlank
  private String tripCode;

  private String driverName;

  @NotBlank
  private String fromLocation;

  @NotBlank
  private String toLocation;

  @NotNull
  private LocalDate startDate;

  @NotNull
  @PositiveOrZero
  private BigDecimal freightAmount;

  private String notes;

  public TripCreateRequest() {}

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public UUID getPartyId() {
    return partyId;
  }

  public void setPartyId(UUID partyId) {
    this.partyId = partyId;
  }

  public String getTripCode() {
    return tripCode;
  }

  public void setTripCode(String tripCode) {
    this.tripCode = tripCode;
  }

  public String getDriverName() {
    return driverName;
  }

  public void setDriverName(String driverName) {
    this.driverName = driverName;
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

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public BigDecimal getFreightAmount() {
    return freightAmount;
  }

  public void setFreightAmount(BigDecimal freightAmount) {
    this.freightAmount = freightAmount;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
