package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface RecentTripProjection {
  UUID getId();

  String getFromLocation();

  String getToLocation();

  String getStatus();

  BigDecimal getFreightAmount();

  LocalDate getStartDate();

  UUID getTruckId();

  String getTruckNumber();
}
