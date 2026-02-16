package com.truckbook.api.controller.dto.truckcosts;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TyreCreateRequest {
  @NotNull
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate purchasedOn;

  @NotNull
  @Positive
  private BigDecimal amount;

  private String brand;

  @Min(1)
  private Integer tyreCount;

  private String notes;

  public TyreCreateRequest() {}

  public LocalDate getPurchasedOn() {
    return purchasedOn;
  }

  public void setPurchasedOn(LocalDate purchasedOn) {
    this.purchasedOn = purchasedOn;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Integer getTyreCount() {
    return tyreCount;
  }

  public void setTyreCount(Integer tyreCount) {
    this.tyreCount = tyreCount;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
