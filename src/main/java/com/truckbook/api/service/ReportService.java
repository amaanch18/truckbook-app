package com.truckbook.api.service;

import com.truckbook.api.controller.dto.reports.OperatingVsRevenueReportResponse;
import com.truckbook.api.controller.dto.reports.OverviewReportResponse;
import com.truckbook.api.controller.dto.reports.ProfitReportResponse;
import java.time.LocalDate;
import java.util.UUID;

public interface ReportService {
  OverviewReportResponse overview(UUID orgId, LocalDate from, LocalDate to, String groupBy, UUID truckId, UUID partyId);

  ProfitReportResponse profit(UUID orgId, LocalDate from, LocalDate to, String groupBy, UUID truckId, UUID partyId);

  OperatingVsRevenueReportResponse operatingVsRevenue(
      UUID orgId, LocalDate from, LocalDate to, String groupBy, UUID truckId, UUID partyId);
}
