package com.truckbook.api.service;

import com.truckbook.api.controller.dto.DashboardResponse;
import java.util.UUID;

public interface DashboardService {
  DashboardResponse getDashboard(UUID orgId);
}
