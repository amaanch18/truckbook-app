package com.truckbook.api.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SettlementCreateRequest {
  @NotNull
  private UUID partyId;

  private UUID truckId;

  @NotNull
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate settlementDate;

  @NotNull
  @Positive
  private BigDecimal receivedAmount;

  @NotBlank
  private String paymentMode;

  private String reference;
  private String notes;

  public SettlementCreateRequest() {}

  public UUID getPartyId() {
    return partyId;
  }

  public void setPartyId(UUID partyId) {
    this.partyId = partyId;
  }

  public UUID getTruckId() {
    return truckId;
  }

  public void setTruckId(UUID truckId) {
    this.truckId = truckId;
  }

  public LocalDate getSettlementDate() {
    return settlementDate;
  }

  public void setSettlementDate(LocalDate settlementDate) {
    this.settlementDate = settlementDate;
  }

  public BigDecimal getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(BigDecimal receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public String getPaymentMode() {
    return paymentMode;
  }

  public void setPaymentMode(String paymentMode) {
    this.paymentMode = paymentMode;
  }

  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
