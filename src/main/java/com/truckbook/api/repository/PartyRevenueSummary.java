package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface PartyRevenueSummary {
  UUID getPartyId();
  String getPartyName();
  BigDecimal getRevenueEarned();
}
