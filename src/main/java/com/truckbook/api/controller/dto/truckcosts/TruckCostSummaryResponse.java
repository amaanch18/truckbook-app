package com.truckbook.api.controller.dto.truckcosts;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TruckCostSummaryResponse {
  private UUID truckId;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate from;

  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate to;

  private BigDecimal repairsTotal;
  private BigDecimal tyresTotal;
  private BigDecimal total;

  public TruckCostSummaryResponse() {}

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public LocalDate getFrom() {
    return from;
  }

  public void setFrom(LocalDate from) {
    this.from = from;
  }

  public LocalDate getTo() {
    return to;
  }

  public void setTo(LocalDate to) {
    this.to = to;
  }

  public BigDecimal getRepairsTotal() {
    return repairsTotal;
  }

  public void setRepairsTotal(BigDecimal repairsTotal) {
    this.repairsTotal = repairsTotal;
  }

  public BigDecimal getTyresTotal() {
    return tyresTotal;
  }

  public void setTyresTotal(BigDecimal tyresTotal) {
    this.tyresTotal = tyresTotal;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }
}
