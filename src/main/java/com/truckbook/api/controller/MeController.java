package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.MeResponse;
import com.truckbook.api.entity.AppUser;
import com.truckbook.api.entity.Organization;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.AppUserRepository;
import com.truckbook.api.repository.OrganizationRepository;
import com.truckbook.api.security.OrgContext;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {
  private final AppUserRepository appUserRepository;
  private final OrganizationRepository organizationRepository;

  public MeController(
      AppUserRepository appUserRepository,
      OrganizationRepository organizationRepository) {
    this.appUserRepository = appUserRepository;
    this.organizationRepository = organizationRepository;
  }

  @GetMapping
  public MeResponse me() {
    UUID userId = OrgContext.requireUserId();
    UUID orgId = OrgContext.requireOrgId();

    AppUser user = appUserRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found"));
    Organization org = organizationRepository.findById(orgId)
        .orElseThrow(() -> new NotFoundException("Organization not found"));

    MeResponse response = new MeResponse();
    response.setUserId(user.getId());
    response.setOrgId(org.getId());
    response.setPhoneE164(user.getPhoneE164());
    response.setDisplayName(user.getDisplayName());
    response.setOnboardingCompleted(org.getOnboardingCompleted());
    if (Boolean.TRUE.equals(org.getOnboardingCompleted())) {
      response.setOrgName(org.getName());
    } else {
      response.setOrgName(null);
    }
    return response;
  }
}
