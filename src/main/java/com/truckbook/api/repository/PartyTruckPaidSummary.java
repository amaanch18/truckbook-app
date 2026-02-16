package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PartyTruckPaidSummary {
  UUID getTruckId();
  BigDecimal getTotalPaid();
}
