package com.truckbook.api.service;

import com.truckbook.api.controller.dto.TripCreateRequest;
import com.truckbook.api.controller.dto.TripResponse;
import com.truckbook.api.controller.dto.TripUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TripService {
  TripResponse create(UUID orgId, TripCreateRequest request);

  List<TripResponse> list(UUID orgId, String status, UUID truckId, UUID partyId, LocalDate dateFrom, LocalDate dateTo);

  TripResponse get(UUID orgId, UUID id);

  TripResponse update(UUID orgId, UUID id, TripUpdateRequest request);

  TripResponse complete(UUID orgId, UUID id);

  String delete(UUID orgId, UUID id);
}
