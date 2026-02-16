package com.truckbook.api.controller.dto.truckcosts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TyreResponse {
  private UUID id;
  private UUID truckId;
  private LocalDate purchasedOn;
  private BigDecimal amount;
  private String brand;
  private Integer tyreCount;
  private String notes;
  private OffsetDateTime createdAt;

  public TyreResponse() {}

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

  public LocalDate getPurchasedOn() {
    return purchasedOn;
  }

  public void setPurchasedOn(LocalDate purchasedOn) {
    this.purchasedOn = purchasedOn;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Integer getTyreCount() {
    return tyreCount;
  }

  public void setTyreCount(Integer tyreCount) {
    this.tyreCount = tyreCount;
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
