package com.truckbook.api.controller.dto;

public class TruckComplianceResponse {
  private TruckComplianceItemResponse insurance;
  private TruckComplianceItemResponse permit;
  private TruckComplianceItemResponse fitness;

  public TruckComplianceResponse() {}

  public TruckComplianceItemResponse getInsurance() {
    return insurance;
  }

  public void setInsurance(TruckComplianceItemResponse insurance) {
    this.insurance = insurance;
  }

  public TruckComplianceItemResponse getPermit() {
    return permit;
  }

  public void setPermit(TruckComplianceItemResponse permit) {
    this.permit = permit;
  }

  public TruckComplianceItemResponse getFitness() {
    return fitness;
  }

  public void setFitness(TruckComplianceItemResponse fitness) {
    this.fitness = fitness;
  }
}
