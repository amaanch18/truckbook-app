package com.truckbook.api.security;

import java.util.UUID;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class OrgContext {
  private OrgContext() {}

  public static UUID requireOrgId() {
    AuthPrincipal principal = requirePrincipal();
    return principal.getOrgId();
  }

  public static UUID requireUserId() {
    AuthPrincipal principal = requirePrincipal();
    return principal.getUserId();
  }

  private static AuthPrincipal requirePrincipal() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal)) {
      throw new AuthenticationCredentialsNotFoundException("Unauthorized");
    }
    return (AuthPrincipal) authentication.getPrincipal();
  }
}
