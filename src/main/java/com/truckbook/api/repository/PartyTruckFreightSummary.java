package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PartyTruckFreightSummary {
  UUID getTruckId();
  String getTruckNumber();
  BigDecimal getTotalFreight();
}
