package com.truckbook.api.controller.dto;

import java.math.BigDecimal;

public class PendingSettlementDto {
  private BigDecimal amount;

  public PendingSettlementDto() {}

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
