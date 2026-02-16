package com.truckbook.api.controller;

import com.truckbook.api.controller.dto.DashboardResponse;
import com.truckbook.api.security.OrgContext;
import com.truckbook.api.service.DashboardService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping
  public DashboardResponse getDashboard() {
    UUID orgId = OrgContext.requireOrgId();
    return dashboardService.getDashboard(orgId);
  }
}
