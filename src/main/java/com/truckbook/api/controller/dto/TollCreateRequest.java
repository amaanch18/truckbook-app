package com.truckbook.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TollCreateRequest {
  @NotNull
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate paidOn;

  @NotNull
  @Positive
  private BigDecimal amount;

  private String plazaName;
  private String notes;

  public TollCreateRequest() {}

  public LocalDate getPaidOn() {
    return paidOn;
  }

  public void setPaidOn(LocalDate paidOn) {
    this.paidOn = paidOn;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getPlazaName() {
    return plazaName;
  }

  public void setPlazaName(String plazaName) {
    this.plazaName = plazaName;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
