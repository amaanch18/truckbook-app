package com.truckbook.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions", schema = "truckbook")
public class Subscription {

  @Id
  @Column(nullable = false)
  private UUID id;

  @Column(name = "org_id", nullable = false)
  private UUID orgId;

  @Column(name = "plan_code", nullable = false)
  private String planCode;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "trial_ends_at")
  private OffsetDateTime trialEndsAt;

  @Column(name = "current_period_start")
  private OffsetDateTime currentPeriodStart;

  @Column(name = "current_period_end")
  private OffsetDateTime currentPeriodEnd;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public Subscription() {}

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

  public String getPlanCode() {
    return planCode;
  }

  public void setPlanCode(String planCode) {
    this.planCode = planCode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public OffsetDateTime getTrialEndsAt() {
    return trialEndsAt;
  }

  public void setTrialEndsAt(OffsetDateTime trialEndsAt) {
    this.trialEndsAt = trialEndsAt;
  }

  public OffsetDateTime getCurrentPeriodStart() {
    return currentPeriodStart;
  }

  public void setCurrentPeriodStart(OffsetDateTime currentPeriodStart) {
    this.currentPeriodStart = currentPeriodStart;
  }

  public OffsetDateTime getCurrentPeriodEnd() {
    return currentPeriodEnd;
  }

  public void setCurrentPeriodEnd(OffsetDateTime currentPeriodEnd) {
    this.currentPeriodEnd = currentPeriodEnd;
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
