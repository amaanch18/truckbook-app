package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PartyFreightSummary {
  UUID getPartyId();
  String getPartyName();
  BigDecimal getTotalFreight();
}
