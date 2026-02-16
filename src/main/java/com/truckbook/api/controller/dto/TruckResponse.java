package com.truckbook.api.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TruckResponse {
  private UUID id;
  private UUID orgId;
  private String truckNumber;
  private String truckType;
  private String status;
  private String notes;
  private TruckComplianceResponse compliance;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public TruckResponse() {}

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

  public TruckComplianceResponse getCompliance() {
    return compliance;
  }

  public void setCompliance(TruckComplianceResponse compliance) {
    this.compliance = compliance;
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
