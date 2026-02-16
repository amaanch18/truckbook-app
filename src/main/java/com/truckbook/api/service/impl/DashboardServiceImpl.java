package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.CountsDto;
import com.truckbook.api.controller.dto.DashboardResponse;
import com.truckbook.api.controller.dto.PendingSettlementDto;
import com.truckbook.api.controller.dto.RecentTripDto;
import com.truckbook.api.controller.dto.TruckMiniDto;
import com.truckbook.api.repository.RecentTripProjection;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TruckRepository;
import com.truckbook.api.service.DashboardService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
  private final TruckRepository truckRepository;
  private final TripRepository tripRepository;

  public DashboardServiceImpl(
      TruckRepository truckRepository,
      TripRepository tripRepository) {
    this.truckRepository = truckRepository;
    this.tripRepository = tripRepository;
  }

  @Override
  public DashboardResponse getDashboard(UUID orgId) {
    long trucksCount = truckRepository.countByOrgId(orgId);
    long tripsCount = tripRepository.countByOrgId(orgId);

    BigDecimal pendingAmount = tripRepository.sumPendingSettlementByOrgId(orgId);

    Sort sort = Sort.by(Sort.Order.desc("startDate"), Sort.Order.desc("createdAt"));
    List<RecentTripProjection> recentTrips =
        tripRepository.findRecentTripsByOrgId(orgId, PageRequest.of(0, 3, sort)).getContent();

    List<RecentTripDto> recentTripDtos = recentTrips.stream()
        .map(this::toRecentTripDto)
        .toList();

    CountsDto counts = new CountsDto();
    counts.setTrucks(trucksCount);
    counts.setTrips(tripsCount);

    PendingSettlementDto pending = new PendingSettlementDto();
    pending.setAmount(pendingAmount == null ? BigDecimal.ZERO : pendingAmount);

    DashboardResponse response = new DashboardResponse();
    response.setCounts(counts);
    response.setPendingSettlement(pending);
    response.setRecentTrips(recentTripDtos);
    return response;
  }

  private RecentTripDto toRecentTripDto(RecentTripProjection projection) {
    RecentTripDto dto = new RecentTripDto();
    dto.setId(projection.getId());
    dto.setFromLocation(projection.getFromLocation());
    dto.setToLocation(projection.getToLocation());
    dto.setStatus(projection.getStatus());
    dto.setFreightAmount(projection.getFreightAmount());
    dto.setStartDate(projection.getStartDate());

    TruckMiniDto truck = new TruckMiniDto();
    truck.setId(projection.getTruckId());
    truck.setTruckNumber(projection.getTruckNumber());
    dto.setTruck(truck);
    return dto;
  }
}
