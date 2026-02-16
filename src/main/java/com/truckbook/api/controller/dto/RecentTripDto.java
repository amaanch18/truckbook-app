package com.truckbook.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RecentTripDto {
  private UUID id;
  private String fromLocation;
  private String toLocation;
  private String status;
  private BigDecimal freightAmount;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  private TruckMiniDto truck;

  public RecentTripDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFromLocation() {
    return fromLocation;
  }

  public void setFromLocation(String fromLocation) {
    this.fromLocation = fromLocation;
  }

  public String getToLocation() {
    return toLocation;
  }

  public void setToLocation(String toLocation) {
    this.toLocation = toLocation;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BigDecimal getFreightAmount() {
    return freightAmount;
  }

  public void setFreightAmount(BigDecimal freightAmount) {
    this.freightAmount = freightAmount;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public TruckMiniDto getTruck() {
    return truck;
  }

  public void setTruck(TruckMiniDto truck) {
    this.truck = truck;
  }
}
