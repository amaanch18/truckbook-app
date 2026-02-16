package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.SubscriptionResponseDto;
import com.truckbook.api.entity.Subscription;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.SubscriptionRepository;
import com.truckbook.api.security.OrgContext;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
  private final SubscriptionRepository subscriptionRepository;

  public SubscriptionController(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  @GetMapping("/current")
  public SubscriptionResponseDto current() {
    UUID orgId = OrgContext.requireOrgId();
    Subscription subscription = subscriptionRepository.findByOrgId(orgId)
        .orElseThrow(() -> new NotFoundException("Subscription not found"));
    return toResponse(subscription);
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
