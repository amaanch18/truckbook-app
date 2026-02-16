package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface TruckPaidSummary {
  UUID getTruckId();
  BigDecimal getTotalPaid();
}
