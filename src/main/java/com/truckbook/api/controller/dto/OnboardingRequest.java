package com.truckbook.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OnboardingRequest {
  @NotBlank
  @Size(min = 2, max = 120)
  private String businessName;

  @Size(max = 120)
  private String ownerDisplayName;

  @Size(max = 100)
  private String city;

  public OnboardingRequest() {}

  public String getBusinessName() {
    return businessName;
  }

  public void setBusinessName(String businessName) {
    this.businessName = businessName;
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
