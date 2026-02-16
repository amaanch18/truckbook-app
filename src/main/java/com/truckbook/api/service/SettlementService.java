package com.truckbook.api.service;

import com.truckbook.api.controller.dto.OutstandingPartyRow;
import com.truckbook.api.controller.dto.OutstandingPartyTruckRow;
import com.truckbook.api.controller.dto.OutstandingTripRow;
import com.truckbook.api.controller.dto.OutstandingTruckRow;
import com.truckbook.api.controller.dto.SettlementAllocateRequest;
import com.truckbook.api.controller.dto.SettlementCreateRequest;
import com.truckbook.api.controller.dto.SettlementDetailResponse;
import com.truckbook.api.controller.dto.SettlementResponse;
import java.util.List;
import java.util.UUID;

public interface SettlementService {
  SettlementResponse create(UUID orgId, SettlementCreateRequest request);

  List<SettlementResponse> list(UUID orgId);

  SettlementDetailResponse get(UUID orgId, UUID id);

  SettlementDetailResponse allocate(UUID orgId, UUID settlementId, SettlementAllocateRequest request);

  List<OutstandingPartyRow> outstandingByParty(UUID orgId);

  List<OutstandingTruckRow> outstandingByTruck(UUID orgId);

  List<OutstandingTripRow> outstandingTripsByParty(UUID orgId, UUID partyId);

  List<OutstandingTripRow> outstandingTripsByTruck(UUID orgId, UUID truckId);

  List<OutstandingPartyTruckRow> outstandingTrucksByParty(UUID orgId, UUID partyId);

  java.math.BigDecimal partyCredit(UUID orgId, UUID partyId);
}
