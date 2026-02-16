package com.truckbook.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "fuel_logs", schema = "truckbook")
public class TripFuelLog {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "trip_id", nullable = false)
  private UUID tripId;

  @Column(name = "entry_date", nullable = false)
  private LocalDate entryDate;

  @Column(name = "liters")
  private BigDecimal liters;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "odometer_km")
  private BigDecimal odometerKm;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public TripFuelLog() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOrgId() {
    return orgId;
  }

  public void setOrgId(UUID orgId) {
    this.orgId = orgId;
  }

  public UUID getTripId() {
    return tripId;
  }

  public void setTripId(UUID tripId) {
    this.tripId = tripId;
  }

  public LocalDate getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDate entryDate) {
    this.entryDate = entryDate;
  }

  public BigDecimal getLiters() {
    return liters;
  }

  public void setLiters(BigDecimal liters) {
    this.liters = liters;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
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
