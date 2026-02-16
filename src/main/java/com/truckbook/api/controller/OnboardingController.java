package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.OnboardingRequest;
import com.truckbook.api.controller.dto.OnboardingResponse;
import com.truckbook.api.entity.AppUser;
import com.truckbook.api.entity.Organization;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.AppUserRepository;
import com.truckbook.api.repository.OrganizationRepository;
import com.truckbook.api.security.OrgContext;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {
  private final OrganizationRepository organizationRepository;
  private final AppUserRepository appUserRepository;

  public OnboardingController(
      OrganizationRepository organizationRepository,
      AppUserRepository appUserRepository) {
    this.organizationRepository = organizationRepository;
    this.appUserRepository = appUserRepository;
  }

  @PostMapping("/complete")
  public OnboardingResponse complete(@Valid @RequestBody OnboardingRequest request) {
    UUID orgId = OrgContext.requireOrgId();
    UUID userId = OrgContext.requireUserId();
    Organization org = organizationRepository.findById(orgId)
        .orElseThrow(() -> new NotFoundException("Organization not found"));

    String businessName = request.getBusinessName() == null ? null : request.getBusinessName().trim();
    org.setName(businessName);
    if (request.getOwnerDisplayName() != null) {
      String ownerDisplayName = request.getOwnerDisplayName().trim();
      org.setOwnerDisplayName(ownerDisplayName);
      if (!ownerDisplayName.isEmpty()) {
        AppUser user = appUserRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        user.setDisplayName(ownerDisplayName);
        appUserRepository.save(user);
      }
    }
    if (request.getCity() != null) {
      org.setCity(request.getCity().trim());
    }
    org.setOnboardingCompleted(true);
    Organization saved = organizationRepository.save(org);

    OnboardingResponse response = new OnboardingResponse();
    response.setOrgId(saved.getId());
    response.setOrgName(saved.getName());
    response.setOnboardingCompleted(saved.getOnboardingCompleted());
    response.setOwnerDisplayName(saved.getOwnerDisplayName());
    response.setCity(saved.getCity());
    return response;
  }
}
