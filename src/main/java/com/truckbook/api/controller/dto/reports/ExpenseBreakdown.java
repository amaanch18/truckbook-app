package com.truckbook.api.controller.dto.reports;

import java.math.BigDecimal;

public class ExpenseBreakdown {
  private BigDecimal fuel;
  private BigDecimal tolls;
  private BigDecimal driver;
  private BigDecimal repairs;
  private BigDecimal tyres;

  public ExpenseBreakdown() {}

  public BigDecimal getFuel() {
    return fuel;
  }

  public void setFuel(BigDecimal fuel) {
    this.fuel = fuel;
  }

  public BigDecimal getTolls() {
    return tolls;
  }

  public void setTolls(BigDecimal tolls) {
    this.tolls = tolls;
  }

  public BigDecimal getDriver() {
    return driver;
  }

  public void setDriver(BigDecimal driver) {
    this.driver = driver;
  }

  public BigDecimal getRepairs() {
    return repairs;
  }

  public void setRepairs(BigDecimal repairs) {
    this.repairs = repairs;
  }

  public BigDecimal getTyres() {
    return tyres;
  }

  public void setTyres(BigDecimal tyres) {
    this.tyres = tyres;
  }
}
