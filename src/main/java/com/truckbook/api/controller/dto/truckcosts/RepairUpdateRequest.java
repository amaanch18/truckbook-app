package com.truckbook.api.controller.dto.truckcosts;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RepairUpdateRequest {
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate repairedOn;

  @Positive
  private BigDecimal amount;

  private String vendorName;
  private String description;

  @Min(0)
  private Integer odometerKm;

  private String notes;

  public RepairUpdateRequest() {}

  public LocalDate getRepairedOn() {
    return repairedOn;
  }

  public void setRepairedOn(LocalDate repairedOn) {
    this.repairedOn = repairedOn;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getOdometerKm() {
    return odometerKm;
  }

  public void setOdometerKm(Integer odometerKm) {
    this.odometerKm = odometerKm;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
