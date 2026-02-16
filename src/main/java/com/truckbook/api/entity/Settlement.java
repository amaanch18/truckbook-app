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
@Table(name = "settlements", schema = "truckbook")
public class Settlement {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "settlement_code", nullable = false)
  private String settlementCode;

  @Column(name = "party_id", nullable = false)
  private UUID partyId;

  @Column(name = "truck_id")
  private UUID truckId;

  @Column(name = "settlement_date", nullable = false)
  private LocalDate settlementDate;

  @Column(name = "received_amount", nullable = false)
  private BigDecimal receivedAmount;

  @Column(name = "unallocated_amount", nullable = false)
  private BigDecimal unallocatedAmount;

  @Column(name = "mode", nullable = false)
  private String mode;

  @Column(name = "reference")
  private String reference;

  @Column(name = "notes")
  private String notes;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public Settlement() {}

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

  public BigDecimal getUnallocatedAmount() {
    return unallocatedAmount;
  }

  public void setUnallocatedAmount(BigDecimal unallocatedAmount) {
    this.unallocatedAmount = unallocatedAmount;
  }

  public String getMode() {
    return mode;
  }

  public void setMode(String mode) {
    this.mode = mode;
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

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
