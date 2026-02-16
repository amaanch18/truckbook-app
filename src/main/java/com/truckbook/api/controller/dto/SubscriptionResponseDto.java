package com.truckbook.api.controller.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class SubscriptionResponseDto {
  private UUID orgId;
  private String planCode;
  private String status;
  private OffsetDateTime trialEndsAt;
  private OffsetDateTime currentPeriodStart;
  private OffsetDateTime currentPeriodEnd;

  public SubscriptionResponseDto() {}

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
}
