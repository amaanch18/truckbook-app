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
@Table(name = "tyre_expenses", schema = "truckbook")
public class TruckTyreExpense {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "truck_id", nullable = false)
  private UUID truckId;

  @Column(name = "entry_date", nullable = false)
  private LocalDate entryDate;

  @Column(name = "tyre_position")
  private String tyrePosition;

  @Column(name = "description")
  private String description;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public TruckTyreExpense() {}

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

  public LocalDate getEntryDate() {
    return entryDate;
  }

  public void setEntryDate(LocalDate entryDate) {
    this.entryDate = entryDate;
  }

  public String getTyrePosition() {
    return tyrePosition;
  }

  public void setTyrePosition(String tyrePosition) {
    this.tyrePosition = tyrePosition;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
