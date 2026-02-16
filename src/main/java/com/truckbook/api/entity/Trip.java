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
@Table(name = "trips", schema = "truckbook")
public class Trip {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "truck_id")
  private UUID truckId;

  @Column(name = "party_id")
  private UUID partyId;

  @Column(name = "trip_code", nullable = false)
  private String tripCode;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "driver_name")
  private String driverName;

  @Column(name = "from_location", nullable = false)
  private String fromLocation;

  @Column(name = "to_location", nullable = false)
  private String toLocation;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "freight_amount", nullable = false)
  private BigDecimal freightAmount;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public Trip() {}

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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
