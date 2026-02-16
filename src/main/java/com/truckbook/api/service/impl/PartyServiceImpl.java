package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.PartyCreateRequest;
import com.truckbook.api.controller.dto.PartyResponse;
import com.truckbook.api.controller.dto.PartyUpdateRequest;
import com.truckbook.api.entity.Party;
import com.truckbook.api.exception.ConflictException;
import com.truckbook.api.exception.NotFoundException;
import com.truckbook.api.repository.PartyRepository;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.service.PartyService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PartyServiceImpl implements PartyService {
  private final PartyRepository partyRepository;
  private final TripRepository tripRepository;

  public PartyServiceImpl(PartyRepository partyRepository, TripRepository tripRepository) {
    this.partyRepository = partyRepository;
    this.tripRepository = tripRepository;
  }

  @Override
  public PartyResponse create(UUID orgId, PartyCreateRequest request) {
    String name = normalizeName(request.getName());
    partyRepository.findByOrgIdAndNameIgnoreCase(orgId, name)
        .ifPresent(existing -> {
          throw new ConflictException("Party already exists");
        });

    OffsetDateTime now = OffsetDateTime.now();
    Party party = new Party();
    party.setId(UUID.randomUUID());
    party.setOrgId(orgId);
    party.setName(name);
    party.setPhone(request.getPhone());
    party.setCreditAmount(BigDecimal.ZERO);
    party.setCreatedAt(now);
    party.setUpdatedAt(now);
    return toResponse(partyRepository.save(party));
  }

  @Override
  public List<PartyResponse> list(UUID orgId, String q) {
    List<Party> parties;
    if (q == null || q.isBlank()) {
      parties = partyRepository.findAllByOrgIdOrderByNameAsc(orgId);
    } else {
      parties = partyRepository.findByOrgIdAndNameContainingIgnoreCaseOrderByNameAsc(orgId, q.trim());
    }
    return parties.stream().map(this::toResponse).toList();
  }

  @Override
  public PartyResponse get(UUID orgId, UUID id) {
    Party party = partyRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Party not found"));
    return toResponse(party);
  }

  @Override
  public PartyResponse update(UUID orgId, UUID id, PartyUpdateRequest request) {
    Party party = partyRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Party not found"));

    String name = normalizeName(request.getName());
    partyRepository.findByOrgIdAndNameIgnoreCase(orgId, name)
        .ifPresent(existing -> {
          if (!existing.getId().equals(party.getId())) {
            throw new ConflictException("Party already exists");
          }
        });

    party.setName(name);
    party.setPhone(request.getPhone());
    party.setUpdatedAt(OffsetDateTime.now());
    return toResponse(partyRepository.save(party));
  }

  @Override
  public String delete(UUID orgId, UUID id) {
    Party party = partyRepository.findByOrgIdAndId(orgId, id)
        .orElseThrow(() -> new NotFoundException("Party not found"));

    if (tripRepository.existsByOrgIdAndPartyId(orgId, id)) {
      throw new ConflictException("Party is used in trips and cannot be deleted");
    }

    partyRepository.delete(party);
    return "Party deleted successfully";
  }

  private PartyResponse toResponse(Party party) {
    PartyResponse response = new PartyResponse();
    response.setId(party.getId());
    response.setOrgId(party.getOrgId());
    response.setName(party.getName());
    response.setPhone(party.getPhone());
    response.setCreatedAt(party.getCreatedAt());
    response.setUpdatedAt(party.getUpdatedAt());
    return response;
  }

  private String normalizeName(String name) {
    return name == null ? null : name.trim();
  }
}
