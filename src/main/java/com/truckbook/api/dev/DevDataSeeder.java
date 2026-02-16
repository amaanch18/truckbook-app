package com.truckbook.api.dev;

import com.truckbook.api.entity.AppUser;
import com.truckbook.api.entity.Organization;
import com.truckbook.api.entity.Party;
import com.truckbook.api.entity.Trip;
import com.truckbook.api.entity.Truck;
import com.truckbook.api.repository.AppUserRepository;
import com.truckbook.api.repository.OrganizationRepository;
import com.truckbook.api.repository.PartyRepository;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TruckRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
  private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

  private final OrganizationRepository organizationRepository;
  private final AppUserRepository appUserRepository;
  private final TruckRepository truckRepository;
  private final PartyRepository partyRepository;
  private final TripRepository tripRepository;

  public DevDataSeeder(
      OrganizationRepository organizationRepository,
      AppUserRepository appUserRepository,
      TruckRepository truckRepository,
      PartyRepository partyRepository,
      TripRepository tripRepository) {
    this.organizationRepository = organizationRepository;
    this.appUserRepository = appUserRepository;
    this.truckRepository = truckRepository;
    this.partyRepository = partyRepository;
    this.tripRepository = tripRepository;
  }

  @Override
  public void run(String... args) {
    int orgCreated = 0;
    int orgExisting = 0;
    int userCreated = 0;
    int userExisting = 0;
    int truckCreated = 0;
    int truckExisting = 0;
    int partyCreated = 0;
    int partyExisting = 0;
    int tripCreated = 0;
    int tripExisting = 0;

    OffsetDateTime now = OffsetDateTime.now();

    Organization org = organizationRepository.findByName("Aman Roadlines").orElse(null);
    if (org == null) {
      Organization created = new Organization();
      created.setId(UUID.randomUUID());
      created.setName("Aman Roadlines");
      created.setCreatedAt(now);
      created.setOnboardingCompleted(false);
      org = organizationRepository.save(created);
      orgCreated++;
    } else {
      orgExisting++;
    }

    AppUser user = appUserRepository.findByOrgIdAndPhoneE164(org.getId(), "+919999999999")
        .orElse(null);
    if (user == null) {
      AppUser created = new AppUser();
      created.setId(UUID.randomUUID());
      created.setOrgId(org.getId());
      created.setPhoneE164("+919999999999");
      created.setDisplayName("Dev User");
      created.setIsActive(true);
      created.setCreatedAt(now);
      created.setUpdatedAt(now);
      user = appUserRepository.save(created);
      userCreated++;
    } else {
      userExisting++;
    }

    Truck truck = truckRepository.findByOrgIdAndTruckNumber(org.getId(), "MH 01 AB 1880")
        .orElse(null);
    if (truck == null) {
      Truck created = new Truck();
      created.setId(UUID.randomUUID());
      created.setOrgId(org.getId());
      created.setTruckNumber("MH 01 AB 1880");
      created.setStatus("ACTIVE");
      created.setCreatedAt(now);
      created.setUpdatedAt(now);
      truck = truckRepository.save(created);
      truckCreated++;
    } else {
      truckExisting++;
    }

    Party party = partyRepository.findByOrgIdAndNameIgnoreCase(org.getId(), "Ultratech")
        .orElse(null);
    if (party == null) {
      Party created = new Party();
      created.setId(UUID.randomUUID());
      created.setOrgId(org.getId());
      created.setName("Ultratech");
      created.setCreditAmount(java.math.BigDecimal.ZERO);
      created.setCreatedAt(now);
      created.setUpdatedAt(now);
      party = partyRepository.save(created);
      partyCreated++;
    } else {
      partyExisting++;
    }

    Trip trip = tripRepository.findByOrgIdAndTripCode(org.getId(), "TRP-DEV-0001")
        .orElse(null);
    if (trip == null) {
      Trip created = new Trip();
      created.setId(UUID.randomUUID());
      created.setOrgId(org.getId());
      created.setTruckId(truck.getId());
      created.setPartyId(party.getId());
      created.setTripCode("TRP-DEV-0001");
      created.setStatus("ACTIVE");
      created.setFromLocation("Mumbai");
      created.setToLocation("Gujrat");
      created.setStartDate(LocalDate.now());
      created.setFreightAmount(new BigDecimal("30000"));
      created.setCreatedAt(now);
      created.setUpdatedAt(now);
      trip = tripRepository.save(created);
      tripCreated++;
    } else {
      tripExisting++;
    }

    log.info(
        "Dev seed summary (schema=truckbook): organizations created={}, existing={}, users created={}, existing={}, trucks created={}, existing={}, parties created={}, existing={}, trips created={}, existing={}",
        orgCreated, orgExisting,
        userCreated, userExisting,
        truckCreated, truckExisting,
        partyCreated, partyExisting,
        tripCreated, tripExisting);
  }
}
