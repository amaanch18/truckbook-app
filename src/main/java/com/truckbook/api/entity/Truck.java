package com.truckbook.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "trucks", schema = "truckbook")
public class Truck {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "truck_number", nullable = false)
  private String truckNumber;

  @Column(name = "truck_type")
  private String truckType;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "notes")
  private String notes;

  @Column(name = "insurance_status")
  private String insuranceStatus;

  @Column(name = "insurance_expiry")
  private LocalDate insuranceExpiry;

  @Column(name = "permit_status")
  private String permitStatus;

  @Column(name = "permit_expiry")
  private LocalDate permitExpiry;

  @Column(name = "fitness_status")
  private String fitnessStatus;

  @Column(name = "fitness_expiry")
  private LocalDate fitnessExpiry;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public Truck() {}

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

  public String getTruckNumber() {
    return truckNumber;
  }

  public void setTruckNumber(String truckNumber) {
    this.truckNumber = truckNumber;
  }

  public String getTruckType() {
    return truckType;
  }

  public void setTruckType(String truckType) {
    this.truckType = truckType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getInsuranceStatus() {
    return insuranceStatus;
  }

  public void setInsuranceStatus(String insuranceStatus) {
    this.insuranceStatus = insuranceStatus;
  }

  public LocalDate getInsuranceExpiry() {
    return insuranceExpiry;
  }

  public void setInsuranceExpiry(LocalDate insuranceExpiry) {
    this.insuranceExpiry = insuranceExpiry;
  }

  public String getPermitStatus() {
    return permitStatus;
  }

  public void setPermitStatus(String permitStatus) {
    this.permitStatus = permitStatus;
  }

  public LocalDate getPermitExpiry() {
    return permitExpiry;
  }

  public void setPermitExpiry(LocalDate permitExpiry) {
    this.permitExpiry = permitExpiry;
  }

  public String getFitnessStatus() {
    return fitnessStatus;
  }

  public void setFitnessStatus(String fitnessStatus) {
    this.fitnessStatus = fitnessStatus;
  }

  public LocalDate getFitnessExpiry() {
    return fitnessExpiry;
  }

  public void setFitnessExpiry(LocalDate fitnessExpiry) {
    this.fitnessExpiry = fitnessExpiry;
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
