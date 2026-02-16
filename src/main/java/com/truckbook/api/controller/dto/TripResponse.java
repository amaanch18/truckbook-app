package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TripResponse {
  private UUID id;
  private UUID orgId;
  private UUID truckId;
  private UUID partyId;
  private String tripCode;
  private String status;
  private String driverName;
  private String fromLocation;
  private String toLocation;
  private LocalDate startDate;
  private BigDecimal freightAmount;
  private BigDecimal paidAmount;
  private BigDecimal outstandingAmount;
  private String billingStatus;
  private BigDecimal fuelTotal;
  private BigDecimal tollTotal;
  private BigDecimal driverExpenseTotal;
  private BigDecimal totalExpense;
  private String notes;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  public TripResponse() {}

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

  public BigDecimal getFuelTotal() {
    return fuelTotal;
  }

  public void setFuelTotal(BigDecimal fuelTotal) {
    this.fuelTotal = fuelTotal;
  }

  public BigDecimal getTollTotal() {
    return tollTotal;
  }

  public void setTollTotal(BigDecimal tollTotal) {
    this.tollTotal = tollTotal;
  }

  public BigDecimal getDriverExpenseTotal() {
    return driverExpenseTotal;
  }

  public void setDriverExpenseTotal(BigDecimal driverExpenseTotal) {
    this.driverExpenseTotal = driverExpenseTotal;
  }

  public BigDecimal getTotalExpense() {
    return totalExpense;
  }

  public void setTotalExpense(BigDecimal totalExpense) {
    this.totalExpense = totalExpense;
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
