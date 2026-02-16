package com.truckbook.api.controller.dto;

import java.time.LocalDate;

public class TruckComplianceItemRequest {
  private String status;
  private LocalDate expiryDate;

  public TruckComplianceItemRequest() {}

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
