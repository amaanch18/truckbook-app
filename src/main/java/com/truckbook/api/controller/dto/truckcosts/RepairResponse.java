package com.truckbook.api.controller.dto.truckcosts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class RepairResponse {
  private UUID id;
  private UUID truckId;
  private LocalDate repairedOn;
  private BigDecimal amount;
  private String vendorName;
  private String description;
  private Integer odometerKm;
  private String notes;
  private OffsetDateTime createdAt;

  public RepairResponse() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public LocalDate getRepairedOn() {
    return repairedOn;
  }

  public void setRepairedOn(LocalDate repairedOn) {
    this.repairedOn = repairedOn;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
