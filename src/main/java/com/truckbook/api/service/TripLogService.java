package com.truckbook.api.service;

import com.truckbook.api.controller.dto.DriverExpenseCreateRequest;
import com.truckbook.api.controller.dto.DriverExpenseResponse;
import com.truckbook.api.controller.dto.DriverExpenseUpdateRequest;
import com.truckbook.api.controller.dto.FuelLogCreateRequest;
import com.truckbook.api.controller.dto.FuelLogResponse;
import com.truckbook.api.controller.dto.FuelLogUpdateRequest;
import com.truckbook.api.controller.dto.TollCreateRequest;
import com.truckbook.api.controller.dto.TollResponse;
import com.truckbook.api.controller.dto.TollUpdateRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TripLogService {
  FuelLogResponse createFuel(UUID orgId, UUID tripId, FuelLogCreateRequest request);
  List<FuelLogResponse> listFuel(UUID orgId, UUID tripId);
  FuelLogResponse updateFuel(UUID orgId, UUID tripId, UUID fuelId, FuelLogUpdateRequest request);
  void deleteFuel(UUID orgId, UUID tripId, UUID fuelId);

  TollResponse createToll(UUID orgId, UUID tripId, TollCreateRequest request);
  List<TollResponse> listTolls(UUID orgId, UUID tripId);
  TollResponse updateToll(UUID orgId, UUID tripId, UUID tollId, TollUpdateRequest request);
  void deleteToll(UUID orgId, UUID tripId, UUID tollId);

  DriverExpenseResponse createDriverExpense(UUID orgId, UUID tripId, DriverExpenseCreateRequest request);
  List<DriverExpenseResponse> listDriverExpenses(UUID orgId, UUID tripId);
  DriverExpenseResponse updateDriverExpense(UUID orgId, UUID tripId, UUID expenseId, DriverExpenseUpdateRequest request);
  void deleteDriverExpense(UUID orgId, UUID tripId, UUID expenseId);

  BigDecimal fuelTotal(UUID orgId, UUID tripId);
  BigDecimal tollTotal(UUID orgId, UUID tripId);
  BigDecimal driverExpenseTotal(UUID orgId, UUID tripId);
}
