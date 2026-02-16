package com.truckbook.api.controller.dto;

import java.util.UUID;

public class AuthResponse {
  private String token;
  private UUID userId;
  private UUID orgId;
  private String phoneE164;

  public AuthResponse() {}

  public AuthResponse(String token, UUID userId, UUID orgId, String phoneE164) {
    this.token = token;
    this.userId = userId;
    this.orgId = orgId;
    this.phoneE164 = phoneE164;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

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
}
