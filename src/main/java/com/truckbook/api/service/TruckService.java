package com.truckbook.api.service;

import com.truckbook.api.controller.dto.TruckCreateRequest;
import com.truckbook.api.controller.dto.TruckResponse;
import com.truckbook.api.controller.dto.TruckUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface TruckService {
  TruckResponse create(UUID orgId, TruckCreateRequest request);

  List<TruckResponse> list(UUID orgId);

  TruckResponse get(UUID orgId, UUID id);

  TruckResponse update(UUID orgId, UUID id, TruckUpdateRequest request);

  String delete(UUID orgId, UUID id);
}
