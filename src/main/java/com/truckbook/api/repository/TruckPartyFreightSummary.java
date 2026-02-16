package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface TruckPartyFreightSummary {
  UUID getTruckId();
  UUID getPartyId();
  BigDecimal getTotalFreight();
}
