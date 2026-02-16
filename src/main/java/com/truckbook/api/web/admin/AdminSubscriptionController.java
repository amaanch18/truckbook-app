package com.truckbook.api.web.admin;

import com.truckbook.api.controller.dto.AdminActivateSubscriptionRequest;
import com.truckbook.api.controller.dto.SubscriptionResponseDto;
import com.truckbook.api.entity.Subscription;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.repository.SubscriptionRepository;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/subscription")
public class AdminSubscriptionController {
  private static final Set<String> PLAN_CODES = Set.of("STARTER", "GROWTH", "PRO");

  private final SubscriptionRepository subscriptionRepository;

  public AdminSubscriptionController(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  @PostMapping("/activate")
  public SubscriptionResponseDto activate(
      @RequestBody AdminActivateSubscriptionRequest request) {
    validateRequest(request);

    OffsetDateTime now = OffsetDateTime.now();
    String planCode = request.getPlanCode().toUpperCase(Locale.ROOT);
    Subscription subscription = subscriptionRepository.findByOrgId(request.getOrgId())
        .orElseGet(() -> {
          Subscription created = new Subscription();
          created.setId(UUID.randomUUID());
          created.setOrgId(request.getOrgId());
          created.setCreatedAt(now);
          return created;
        });

    subscription.setPlanCode(planCode);
    subscription.setStatus("ACTIVE");
    subscription.setTrialEndsAt(null);
    subscription.setCurrentPeriodStart(now);
    subscription.setCurrentPeriodEnd(now.plusMonths(request.getMonths()));
    subscription.setUpdatedAt(now);

    Subscription saved = subscriptionRepository.save(subscription);
    return toResponse(saved);
  }

  private void validateRequest(AdminActivateSubscriptionRequest request) {
    if (request == null) {
      throw new BadRequestException("Invalid request body");
    }
    if (request.getOrgId() == null) {
      throw new BadRequestException("Validation failed", Map.of("orgId", "orgId is required"));
    }
    if (request.getPlanCode() == null || request.getPlanCode().isBlank()) {
      throw new BadRequestException("Validation failed", Map.of("planCode", "planCode is required"));
    }
    String planCode = request.getPlanCode().toUpperCase(Locale.ROOT);
    if (!PLAN_CODES.contains(planCode)) {
      throw new BadRequestException("Validation failed", Map.of("planCode", "planCode is invalid"));
    }
    Integer months = request.getMonths();
    if (months == null) {
      throw new BadRequestException("Validation failed", Map.of("months", "months is required"));
    }
    if (months < 1 || months > 24) {
      throw new BadRequestException("Validation failed", Map.of("months", "months must be between 1 and 24"));
    }
  }

  private SubscriptionResponseDto toResponse(Subscription subscription) {
    SubscriptionResponseDto response = new SubscriptionResponseDto();
    response.setOrgId(subscription.getOrgId());
    response.setPlanCode(subscription.getPlanCode());
    response.setStatus(subscription.getStatus());
    response.setTrialEndsAt(subscription.getTrialEndsAt());
    response.setCurrentPeriodStart(subscription.getCurrentPeriodStart());
    response.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
    return response;
  }
}
