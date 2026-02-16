package com.truckbook.api.controller.dto;

import java.util.UUID;

public class MeResponse {
  private UUID userId;
  private UUID orgId;
  private String phoneE164;
  private String displayName;
  private Boolean onboardingCompleted;
  private String orgName;

  public MeResponse() {}

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public UUID getOrgId() {
    return orgId;
  }

  public void setOrgId(UUID orgId) {
    this.orgId = orgId;
  }

  public String getPhoneE164() {
    return phoneE164;
  }

  public void setPhoneE164(String phoneE164) {
    this.phoneE164 = phoneE164;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Boolean getOnboardingCompleted() {
    return onboardingCompleted;
  }

  public void setOnboardingCompleted(Boolean onboardingCompleted) {
    this.onboardingCompleted = onboardingCompleted;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }
}
