package com.truckbook.api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class SettlementResponse {
  private UUID id;
  private UUID orgId;
  private String settlementCode;
  private UUID partyId;
  private UUID truckId;
  private LocalDate settlementDate;
  private BigDecimal receivedAmount;
  private String paymentMode;
  private String reference;
  private String notes;
  private BigDecimal allocatedAmount;
  private BigDecimal unallocatedAmount;
  private BigDecimal partyCreditAfter;
  private OffsetDateTime createdAt;

  public SettlementResponse() {}

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

  public String getSettlementCode() {
    return settlementCode;
  }

  public void setSettlementCode(String settlementCode) {
    this.settlementCode = settlementCode;
  }

  public UUID getPartyId() {
    return partyId;
  }

  public void setPartyId(UUID partyId) {
    this.partyId = partyId;
  }

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public LocalDate getSettlementDate() {
    return settlementDate;
  }

  public void setSettlementDate(LocalDate settlementDate) {
    this.settlementDate = settlementDate;
  }

  public BigDecimal getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(BigDecimal receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public String getPaymentMode() {
    return paymentMode;
  }

  public void setPaymentMode(String paymentMode) {
    this.paymentMode = paymentMode;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public BigDecimal getAllocatedAmount() {
    return allocatedAmount;
  }

  public void setAllocatedAmount(BigDecimal allocatedAmount) {
    this.allocatedAmount = allocatedAmount;
  }

  public BigDecimal getUnallocatedAmount() {
    return unallocatedAmount;
  }

  public void setUnallocatedAmount(BigDecimal unallocatedAmount) {
    this.unallocatedAmount = unallocatedAmount;
  }

  public BigDecimal getPartyCreditAfter() {
    return partyCreditAfter;
  }

  public void setPartyCreditAfter(BigDecimal partyCreditAfter) {
    this.partyCreditAfter = partyCreditAfter;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
