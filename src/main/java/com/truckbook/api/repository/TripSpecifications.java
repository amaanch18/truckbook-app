package com.truckbook.api.repository;

import com.truckbook.api.entity.Trip;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class TripSpecifications {
  private TripSpecifications() {}

  public static Specification<Trip> orgId(UUID orgId) {
    return (root, query, cb) -> cb.equal(root.get("orgId"), orgId);
  }

  public static Specification<Trip> status(String status) {
    return (root, query, cb) -> cb.equal(cb.upper(root.get("status")), status.toUpperCase());
  }

  public static Specification<Trip> truckId(UUID truckId) {
    return (root, query, cb) -> cb.equal(root.get("truckId"), truckId);
  }

  public static Specification<Trip> partyId(UUID partyId) {
    return (root, query, cb) -> cb.equal(root.get("partyId"), partyId);
  }

  public static Specification<Trip> startDateFrom(LocalDate dateFrom) {
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), dateFrom);
  }

  public static Specification<Trip> startDateTo(LocalDate dateTo) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startDate"), dateTo);
  }
}
