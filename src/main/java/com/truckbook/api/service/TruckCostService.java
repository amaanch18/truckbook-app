package com.truckbook.api.service;

import com.truckbook.api.controller.dto.truckcosts.RepairCreateRequest;
import com.truckbook.api.controller.dto.truckcosts.RepairResponse;
import com.truckbook.api.controller.dto.truckcosts.RepairUpdateRequest;
import com.truckbook.api.controller.dto.truckcosts.TruckCostSummaryResponse;
import com.truckbook.api.controller.dto.truckcosts.TyreCreateRequest;
import com.truckbook.api.controller.dto.truckcosts.TyreResponse;
import com.truckbook.api.controller.dto.truckcosts.TyreUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TruckCostService {
  RepairResponse createRepair(UUID orgId, UUID truckId, RepairCreateRequest request);
  List<RepairResponse> listRepairs(UUID orgId, UUID truckId, LocalDate from, LocalDate to);
  RepairResponse updateRepair(UUID orgId, UUID truckId, UUID repairId, RepairUpdateRequest request);
  void deleteRepair(UUID orgId, UUID truckId, UUID repairId);

  TyreResponse createTyre(UUID orgId, UUID truckId, TyreCreateRequest request);
  List<TyreResponse> listTyres(UUID orgId, UUID truckId, LocalDate from, LocalDate to);
  TyreResponse updateTyre(UUID orgId, UUID truckId, UUID tyreId, TyreUpdateRequest request);
  void deleteTyre(UUID orgId, UUID truckId, UUID tyreId);

  TruckCostSummaryResponse summary(UUID orgId, UUID truckId, LocalDate from, LocalDate to);
}
