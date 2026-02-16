package com.truckbook.api.controller;

import com.truckbook.api.repository.AppUserRepository;
import com.truckbook.api.repository.OrganizationRepository;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TruckRepository;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DevDbCheckController {
  private final OrganizationRepository organizationRepository;
  private final AppUserRepository appUserRepository;
  private final TruckRepository truckRepository;
  private final TripRepository tripRepository;

  public DevDbCheckController(
      OrganizationRepository organizationRepository,
      AppUserRepository appUserRepository,
      TruckRepository truckRepository,
      TripRepository tripRepository) {
    this.organizationRepository = organizationRepository;
    this.appUserRepository = appUserRepository;
    this.truckRepository = truckRepository;
    this.tripRepository = tripRepository;
  }

  @GetMapping("/api/dev/db-check")
  public Map<String, Object> dbCheck() {
    return Map.of(
        "schema", "truckbook",
        "organizations", organizationRepository.count(),
        "users", appUserRepository.count(),
        "trucks", truckRepository.count(),
        "trips", tripRepository.count());
  }
}
