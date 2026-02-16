package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DriverExpenseResponse {
  private UUID id;
  private UUID tripId;
  private LocalDate spentOn;
  private String category;
  private BigDecimal amount;
  private String notes;
  private OffsetDateTime createdAt;

  public DriverExpenseResponse() {}

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

  public LocalDate getSpentOn() {
    return spentOn;
  }

  public void setSpentOn(LocalDate spentOn) {
    this.spentOn = spentOn;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
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
