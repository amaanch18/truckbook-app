package com.truckbook.api.controller.dto;

public class TruckUpdateRequest {
  private String truckNumber;
  private String status;
  private String notes;
  private String truckType;
  private TruckComplianceRequest compliance;

  public TruckUpdateRequest() {}

  public String getTruckNumber() {
    return truckNumber;
  }

  public void setTruckNumber(String truckNumber) {
    this.truckNumber = truckNumber;
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

  public String getTruckType() {
    return truckType;
  }

  public void setTruckType(String truckType) {
    this.truckType = truckType;
  }

  public TruckComplianceRequest getCompliance() {
    return compliance;
  }

  public void setCompliance(TruckComplianceRequest compliance) {
    this.compliance = compliance;
  }
}
