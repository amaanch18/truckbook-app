package com.truckbook.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DriverExpenseUpdateRequest {
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate spentOn;

  private DriverExpenseCategory category;

  @Positive
  private BigDecimal amount;

  private String notes;

  public DriverExpenseUpdateRequest() {}

  public LocalDate getSpentOn() {
    return spentOn;
  }

  public void setSpentOn(LocalDate spentOn) {
    this.spentOn = spentOn;
  }

  public DriverExpenseCategory getCategory() {
    return category;
  }

  public void setCategory(DriverExpenseCategory category) {
    this.category = category;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
