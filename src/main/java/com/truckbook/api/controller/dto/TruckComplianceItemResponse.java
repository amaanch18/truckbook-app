package com.truckbook.api.controller.dto;

import java.time.LocalDate;

public class TruckComplianceItemResponse {
  private String status;
  private LocalDate expiryDate;

  public TruckComplianceItemResponse() {}

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }
}
