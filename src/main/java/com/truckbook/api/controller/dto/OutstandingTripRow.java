package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class OutstandingTripRow {
  private UUID tripId;
  private String tripCode;
  private String fromLocation;
  private String toLocation;
  private LocalDate startDate;
  private BigDecimal freightAmount;
  private BigDecimal paidAmount;
  private BigDecimal outstandingAmount;
  private String billingStatus;

  public OutstandingTripRow() {}

  public UUID getTripId() {
    return tripId;
  }

  public void setTripId(UUID tripId) {
    this.tripId = tripId;
  }

  public String getTripCode() {
    return tripCode;
  }

  public void setTripCode(String tripCode) {
    this.tripCode = tripCode;
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

  public BigDecimal getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(BigDecimal paidAmount) {
    this.paidAmount = paidAmount;
  }

  public BigDecimal getOutstandingAmount() {
    return outstandingAmount;
  }

  public void setOutstandingAmount(BigDecimal outstandingAmount) {
    this.outstandingAmount = outstandingAmount;
  }

  public String getBillingStatus() {
    return billingStatus;
  }

  public void setBillingStatus(String billingStatus) {
    this.billingStatus = billingStatus;
  }
}
