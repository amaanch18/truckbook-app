package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TollResponse {
  private UUID id;
  private UUID tripId;
  private LocalDate paidOn;
  private BigDecimal amount;
  private String plazaName;
  private String notes;
  private OffsetDateTime createdAt;

  public TollResponse() {}

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

  public LocalDate getPaidOn() {
    return paidOn;
  }

  public void setPaidOn(LocalDate paidOn) {
    this.paidOn = paidOn;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getPlazaName() {
    return plazaName;
  }

  public void setPlazaName(String plazaName) {
    this.plazaName = plazaName;
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
