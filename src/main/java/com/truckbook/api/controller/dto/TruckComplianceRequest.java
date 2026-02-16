package com.truckbook.api.controller.dto;

public class TruckComplianceRequest {
  private TruckComplianceItemRequest insurance;
  private TruckComplianceItemRequest permit;
  private TruckComplianceItemRequest fitness;

  public TruckComplianceRequest() {}

  public TruckComplianceItemRequest getInsurance() {
    return insurance;
  }

  public void setInsurance(TruckComplianceItemRequest insurance) {
    this.insurance = insurance;
  }

  public TruckComplianceItemRequest getPermit() {
    return permit;
  }

  public void setPermit(TruckComplianceItemRequest permit) {
    this.permit = permit;
  }

  public TruckComplianceItemRequest getFitness() {
    return fitness;
  }

  public void setFitness(TruckComplianceItemRequest fitness) {
    this.fitness = fitness;
  }
}
