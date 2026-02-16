package com.truckbook.api.controller.dto;

import java.util.UUID;

public class OnboardingResponse {
  private UUID orgId;
  private String orgName;
  private Boolean onboardingCompleted;
  private String ownerDisplayName;
  private String city;

  public OnboardingResponse() {}

  public UUID getOrgId() {
    return orgId;
  }

  public void setOrgId(UUID orgId) {
    this.orgId = orgId;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public Boolean getOnboardingCompleted() {
    return onboardingCompleted;
  }

  public void setOnboardingCompleted(Boolean onboardingCompleted) {
    this.onboardingCompleted = onboardingCompleted;
  }

  public String getOwnerDisplayName() {
    return ownerDisplayName;
  }

  public void setOwnerDisplayName(String ownerDisplayName) {
    this.ownerDisplayName = ownerDisplayName;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
}
