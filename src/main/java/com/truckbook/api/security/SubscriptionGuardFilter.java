package com.truckbook.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truckbook.api.entity.Subscription;
import com.truckbook.api.repository.SubscriptionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SubscriptionGuardFilter extends OncePerRequestFilter {
  private final SubscriptionRepository subscriptionRepository;
  private final ObjectMapper objectMapper;

  public SubscriptionGuardFilter(
      SubscriptionRepository subscriptionRepository,
      ObjectMapper objectMapper) {
    this.subscriptionRepository = subscriptionRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      filterChain.doFilter(request, response);
      return;
    }

    UUID orgId = extractOrgId(authentication);
    if (orgId == null) {
      filterChain.doFilter(request, response);
      return;
    }

    Optional<Subscription> subscriptionOpt = subscriptionRepository.findByOrgId(orgId);
    if (subscriptionOpt.isEmpty()) {
      writeExpiredResponse(response);
      return;
    }

    Subscription subscription = subscriptionOpt.get();
    String status = subscription.getStatus();
    if ("ACTIVE".equalsIgnoreCase(status)) {
      filterChain.doFilter(request, response);
      return;
    }

    if ("TRIAL".equalsIgnoreCase(status)) {
      OffsetDateTime trialEndsAt = subscription.getTrialEndsAt();
      OffsetDateTime now = OffsetDateTime.now();
      if (trialEndsAt != null && trialEndsAt.isAfter(now)) {
        filterChain.doFilter(request, response);
        return;
      }
      subscription.setStatus("EXPIRED");
      subscription.setUpdatedAt(now);
      subscriptionRepository.save(subscription);
      writeExpiredResponse(response);
      return;
    }

    writeExpiredResponse(response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    if (path == null) {
      return true;
    }
    return path.startsWith("/api/auth/")
        || path.startsWith("/api/onboarding/")
        || path.startsWith("/api/admin/")
        || path.equals("/api/subscription/current");
  }

  private UUID extractOrgId(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof AuthPrincipal authPrincipal) {
      return authPrincipal.getOrgId();
    }
    return null;
  }

  private void writeExpiredResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    Map<String, Object> body = new HashMap<>();
    body.put("error", "SUBSCRIPTION_EXPIRED");
    body.put("message", "Your subscription has expired. Please upgrade to continue.");
    objectMapper.writeValue(response.getWriter(), body);
  }
}
