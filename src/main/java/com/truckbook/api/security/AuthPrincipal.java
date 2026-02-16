package com.truckbook.api.security;

import java.util.UUID;

public class AuthPrincipal {
  private final UUID userId;
  private final UUID orgId;
  private final String phoneE164;

  public AuthPrincipal(UUID userId, UUID orgId, String phoneE164) {
    this.userId = userId;
    this.orgId = orgId;
    this.phoneE164 = phoneE164;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getOrgId() {
    return orgId;
  }

  public String getPhoneE164() {
    return phoneE164;
  }
}
