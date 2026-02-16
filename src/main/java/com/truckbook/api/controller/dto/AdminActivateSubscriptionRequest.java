package com.truckbook.api.controller.dto;

import java.util.UUID;

public class AdminActivateSubscriptionRequest {
  private UUID orgId;
  private String planCode;
  private Integer months;

  public AdminActivateSubscriptionRequest() {}

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

  public Integer getMonths() {
    return months;
  }

  public void setMonths(Integer months) {
    this.months = months;
  }
}
