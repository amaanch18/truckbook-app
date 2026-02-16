package com.truckbook.api.service;

import com.truckbook.api.controller.dto.PartyCreateRequest;
import com.truckbook.api.controller.dto.PartyResponse;
import com.truckbook.api.controller.dto.PartyUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface PartyService {
  PartyResponse create(UUID orgId, PartyCreateRequest request);

  List<PartyResponse> list(UUID orgId, String q);

  PartyResponse get(UUID orgId, UUID id);

  PartyResponse update(UUID orgId, UUID id, PartyUpdateRequest request);

  String delete(UUID orgId, UUID id);
}
