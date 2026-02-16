package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.reports.OperatingVsRevenueReportResponse;
import com.truckbook.api.controller.dto.reports.OverviewReportResponse;
import com.truckbook.api.controller.dto.reports.ProfitReportResponse;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.ReportService;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final ReportService reportService;

  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }

  @GetMapping("/overview")
  public OverviewReportResponse overview(
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to,
      @RequestParam(required = false) String groupBy,
      @RequestParam(required = false) UUID truckId,
      @RequestParam(required = false) UUID partyId) {
    UUID orgId = OrgContext.requireOrgId();
    return reportService.overview(orgId, from, to, groupBy, truckId, partyId);
  }

  @GetMapping("/profit")
  public ProfitReportResponse profit(
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to,
      @RequestParam(required = false) String groupBy,
      @RequestParam(required = false) UUID truckId,
      @RequestParam(required = false) UUID partyId) {
    UUID orgId = OrgContext.requireOrgId();
    return reportService.profit(orgId, from, to, groupBy, truckId, partyId);
  }

  @GetMapping("/operating-vs-revenue")
  public OperatingVsRevenueReportResponse operatingVsRevenue(
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate from,
      @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate to,
      @RequestParam(required = false) String groupBy,
      @RequestParam(required = false) UUID truckId,
      @RequestParam(required = false) UUID partyId) {
    UUID orgId = OrgContext.requireOrgId();
    return reportService.operatingVsRevenue(orgId, from, to, groupBy, truckId, partyId);
  }
}
