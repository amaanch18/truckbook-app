package com.truckbook.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PartyCreateRequest {
  @NotBlank
  @Size(min = 2, max = 80)
  private String name;
  private String phone;

  public PartyCreateRequest() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
}
