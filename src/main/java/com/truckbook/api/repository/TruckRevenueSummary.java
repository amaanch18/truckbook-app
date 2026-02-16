package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface TruckRevenueSummary {
  UUID getTruckId();
  String getTruckNumber();
  BigDecimal getRevenueEarned();
}
