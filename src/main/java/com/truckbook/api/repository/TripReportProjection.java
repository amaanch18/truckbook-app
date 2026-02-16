package com.truckbook.api.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface TripReportProjection {
  UUID getTripId();
  String getTripCode();
  String getFromLocation();
  String getToLocation();
  UUID getTruckId();
  String getTruckNumber();
  BigDecimal getFreightAmount();
  String getStatus();
  LocalDate getStartDate();
}
