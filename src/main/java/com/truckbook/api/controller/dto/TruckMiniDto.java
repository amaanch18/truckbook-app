package com.truckbook.api.controller.dto;

import java.util.UUID;

public class TruckMiniDto {
  private UUID id;
  private String truckNumber;

  public TruckMiniDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTruckNumber() {
    return truckNumber;
  }

  public void setTruckNumber(String truckNumber) {
    this.truckNumber = truckNumber;
  }
}
