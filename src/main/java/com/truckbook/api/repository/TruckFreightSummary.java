package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface TruckFreightSummary {
  UUID getTruckId();
  String getTruckNumber();
  BigDecimal getTotalFreight();
}
