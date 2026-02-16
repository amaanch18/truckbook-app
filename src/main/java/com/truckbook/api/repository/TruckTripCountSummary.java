package com.truckbook.api.repository;

import java.util.UUID;

public interface TruckTripCountSummary {
  UUID getTruckId();
  Long getTripCount();
}
