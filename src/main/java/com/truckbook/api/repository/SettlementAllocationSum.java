package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface SettlementAllocationSum {
  UUID getSettlementId();
  BigDecimal getTotalApplied();
}
