package com.truckbook.api.service.impl;

import com.truckbook.api.controller.dto.reports.ExpenseBreakdown;
import com.truckbook.api.controller.dto.reports.HighestCostTripRow;
import com.truckbook.api.controller.dto.reports.HighestOverheadTruckRow;
import com.truckbook.api.controller.dto.reports.OperatingSeriesPoint;
import com.truckbook.api.controller.dto.reports.OperatingSummary;
import com.truckbook.api.controller.dto.reports.OperatingVsRevenueReportResponse;
import com.truckbook.api.controller.dto.reports.OverviewReportResponse;
import com.truckbook.api.controller.dto.reports.OverviewSeriesPoint;
import com.truckbook.api.controller.dto.reports.OverviewSummary;
import com.truckbook.api.controller.dto.reports.ProfitReportResponse;
import com.truckbook.api.controller.dto.reports.ProfitSeriesPoint;
import com.truckbook.api.controller.dto.reports.ProfitSummary;
import com.truckbook.api.controller.dto.reports.ReportRange;
import com.truckbook.api.controller.dto.reports.TopPartyRow;
import com.truckbook.api.controller.dto.reports.TopTruckRow;
import com.truckbook.api.controller.dto.reports.TripBreakdownRow;
import com.truckbook.api.controller.dto.reports.TruckSummaryRow;
import com.truckbook.api.exception.BadRequestException;
import com.truckbook.api.entity.Truck;
import com.truckbook.api.repository.PartyReceivedSummary;
import com.truckbook.api.repository.PartyRevenueSummary;
import com.truckbook.api.repository.SettlementAllocationRepository;
import com.truckbook.api.repository.SettlementRepository;
import com.truckbook.api.repository.TruckRepairRepository;
import com.truckbook.api.repository.TruckTyreExpenseRepository;
import com.truckbook.api.repository.TripDriverExpenseRepository;
import com.truckbook.api.repository.TripFuelLogRepository;
import com.truckbook.api.repository.TripRepository;
import com.truckbook.api.repository.TripTollLogRepository;
import com.truckbook.api.repository.TruckExpenseSummary;
import com.truckbook.api.repository.TruckRevenueSummary;
import com.truckbook.api.repository.TruckTripCountSummary;
import com.truckbook.api.repository.TripAmountSum;
import com.truckbook.api.repository.TripReportProjection;
import com.truckbook.api.repository.TruckRepository;
import com.truckbook.api.service.ReportService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl implements ReportService {
  private static final DateTimeFormatter LABEL_DAY = DateTimeFormatter.ofPattern("dd-MM");
  private static final DateTimeFormatter LABEL_MONTH = DateTimeFormatter.ofPattern("MMM yyyy");

  private final TripRepository tripRepository;
  private final SettlementRepository settlementRepository;
  private final SettlementAllocationRepository settlementAllocationRepository;
  private final TripFuelLogRepository fuelLogRepository;
  private final TripTollLogRepository tollLogRepository;
  private final TripDriverExpenseRepository driverExpenseRepository;
  private final TruckRepairRepository truckRepairRepository;
  private final TruckTyreExpenseRepository truckTyreExpenseRepository;
  private final TruckRepository truckRepository;

  public ReportServiceImpl(
      TripRepository tripRepository,
      SettlementRepository settlementRepository,
      SettlementAllocationRepository settlementAllocationRepository,
      TripFuelLogRepository fuelLogRepository,
      TripTollLogRepository tollLogRepository,
      TripDriverExpenseRepository driverExpenseRepository,
      TruckRepairRepository truckRepairRepository,
      TruckTyreExpenseRepository truckTyreExpenseRepository,
      TruckRepository truckRepository) {
    this.tripRepository = tripRepository;
    this.settlementRepository = settlementRepository;
    this.settlementAllocationRepository = settlementAllocationRepository;
    this.fuelLogRepository = fuelLogRepository;
    this.tollLogRepository = tollLogRepository;
    this.driverExpenseRepository = driverExpenseRepository;
    this.truckRepairRepository = truckRepairRepository;
    this.truckTyreExpenseRepository = truckTyreExpenseRepository;
    this.truckRepository = truckRepository;
  }

  @Override
  public OverviewReportResponse overview(UUID orgId, LocalDate from, LocalDate to, String groupBy, UUID truckId, UUID partyId) {
    validateRange(from, to);
    String grouping = normalizeGroupBy(groupBy);

    List<Bucket> buckets = buildBuckets(from, to, grouping);
    List<OverviewSeriesPoint> series = new ArrayList<>();

    BigDecimal totalRevenue = BigDecimal.ZERO;
    BigDecimal totalExpenses = BigDecimal.ZERO;
    BigDecimal totalCash = BigDecimal.ZERO;

    for (Bucket bucket : buckets) {
      BigDecimal revenue = normalizeMoney(tripRepository.sumRevenueCompleted(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal fuel = normalizeMoney(fuelLogRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal toll = normalizeMoney(tollLogRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal driver = normalizeMoney(driverExpenseRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal repairs = normalizeMoney(sumRepairCosts(orgId, bucket.from, bucket.to, truckId));
      BigDecimal tyres = normalizeMoney(sumTyreCosts(orgId, bucket.from, bucket.to, truckId));
      BigDecimal expenses = fuel.add(toll).add(driver).add(repairs).add(tyres);
      BigDecimal cash = normalizeMoney(settlementAllocationRepository.sumAllocatedByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));

      OverviewSeriesPoint point = new OverviewSeriesPoint();
      point.setLabel(bucket.label);
      point.setDateFrom(bucket.from);
      point.setDateTo(bucket.to);
      point.setRevenueEarned(scaleMoney(revenue));
      point.setExpensesTotal(scaleMoney(expenses));
      point.setProfit(scaleMoney(revenue.subtract(expenses)));
      point.setCashReceived(scaleMoney(cash));
      point.setOutstanding(scaleMoney(revenue.subtract(cash)));
      series.add(point);

      totalRevenue = totalRevenue.add(revenue);
      totalExpenses = totalExpenses.add(expenses);
      totalCash = totalCash.add(cash);
    }

    OverviewSummary summary = new OverviewSummary();
    summary.setRevenueEarned(scaleMoney(totalRevenue));
    summary.setExpensesTotal(scaleMoney(totalExpenses));
    summary.setProfit(scaleMoney(totalRevenue.subtract(totalExpenses)));
    summary.setCashReceived(scaleMoney(totalCash));
    summary.setOutstanding(scaleMoney(totalRevenue.subtract(totalCash)));
    summary.setTripCount(tripRepository.countCompletedTrips(orgId, from, to, truckId, partyId));

    List<TopPartyRow> topParties = buildTopParties(orgId, from, to, truckId, partyId);
    List<TopTruckRow> topTrucks = buildTopTrucks(orgId, from, to, truckId, partyId);

    OverviewReportResponse response = new OverviewReportResponse();
    response.setRange(new ReportRange(from, to, grouping));
    response.setSummary(summary);
    response.setSeries(series);
    response.setTopParties(topParties);
    response.setTopTrucks(topTrucks);
    return response;
  }

  @Override
  public ProfitReportResponse profit(UUID orgId, LocalDate from, LocalDate to, String groupBy, UUID truckId, UUID partyId) {
    validateRange(from, to);
    String grouping = normalizeGroupBy(groupBy);

    List<Bucket> buckets = buildBuckets(from, to, grouping);
    List<ProfitSeriesPoint> series = new ArrayList<>();

    BigDecimal totalRevenue = BigDecimal.ZERO;
    BigDecimal totalExpenses = BigDecimal.ZERO;
    BigDecimal totalFuel = BigDecimal.ZERO;
    BigDecimal totalToll = BigDecimal.ZERO;
    BigDecimal totalDriver = BigDecimal.ZERO;

    for (Bucket bucket : buckets) {
      BigDecimal revenue = normalizeMoney(tripRepository.sumRevenueCompleted(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal fuel = normalizeMoney(fuelLogRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal toll = normalizeMoney(tollLogRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal driver = normalizeMoney(driverExpenseRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal repairs = normalizeMoney(sumRepairCosts(orgId, bucket.from, bucket.to, truckId));
      BigDecimal tyres = normalizeMoney(sumTyreCosts(orgId, bucket.from, bucket.to, truckId));
      BigDecimal expenses = fuel.add(toll).add(driver).add(repairs).add(tyres);

      ProfitSeriesPoint point = new ProfitSeriesPoint();
      point.setLabel(bucket.label);
      point.setDateFrom(bucket.from);
      point.setDateTo(bucket.to);
      point.setRevenueEarned(scaleMoney(revenue));
      point.setExpensesTotal(scaleMoney(expenses));
      point.setProfit(scaleMoney(revenue.subtract(expenses)));
      point.setMarginPct(scalePercent(calcPercent(revenue.subtract(expenses), revenue)));
      series.add(point);

      totalRevenue = totalRevenue.add(revenue);
      totalExpenses = totalExpenses.add(expenses);
      totalFuel = totalFuel.add(fuel);
      totalToll = totalToll.add(toll);
      totalDriver = totalDriver.add(driver);
    }

    ProfitSummary summary = new ProfitSummary();
    summary.setRevenueEarned(scaleMoney(totalRevenue));
    summary.setExpensesTotal(scaleMoney(totalExpenses));
    summary.setProfit(scaleMoney(totalRevenue.subtract(totalExpenses)));
    summary.setMarginPct(scalePercent(calcPercent(totalRevenue.subtract(totalExpenses), totalRevenue)));
    summary.setTripCount(tripRepository.countCompletedTrips(orgId, from, to, truckId, partyId));

    ExpenseBreakdown breakdown = new ExpenseBreakdown();
    breakdown.setFuel(scaleMoney(totalFuel));
    breakdown.setTolls(scaleMoney(totalToll));
    breakdown.setDriver(scaleMoney(totalDriver));
    BigDecimal repairsTotal = normalizeMoney(sumRepairCosts(orgId, from, to, truckId));
    BigDecimal tyresTotal = normalizeMoney(sumTyreCosts(orgId, from, to, truckId));
    breakdown.setRepairs(scaleMoney(repairsTotal));
    breakdown.setTyres(scaleMoney(tyresTotal));

    ProfitReportResponse response = new ProfitReportResponse();
    response.setRange(new ReportRange(from, to, grouping));
    response.setSummary(summary);
    response.setSeries(series);
    response.setExpenseBreakdown(breakdown);
    List<TripBreakdownRow> tripBreakdown = buildTripBreakdown(orgId, from, to, truckId, partyId);
    response.setTripBreakdown(tripBreakdown);
    response.setTruckSummary(buildTruckSummary(orgId, from, to, tripBreakdown));
    return response;
  }

  @Override
  public OperatingVsRevenueReportResponse operatingVsRevenue(
      UUID orgId, LocalDate from, LocalDate to, String groupBy, UUID truckId, UUID partyId) {
    validateRange(from, to);
    String grouping = normalizeGroupBy(groupBy);

    List<Bucket> buckets = buildBuckets(from, to, grouping);
    List<OperatingSeriesPoint> series = new ArrayList<>();

    BigDecimal totalRevenue = BigDecimal.ZERO;
    BigDecimal totalOperating = BigDecimal.ZERO;

    for (Bucket bucket : buckets) {
      BigDecimal revenue = normalizeMoney(tripRepository.sumRevenueCompleted(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal fuel = normalizeMoney(fuelLogRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal toll = normalizeMoney(tollLogRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal driver = normalizeMoney(driverExpenseRepository.sumAmountByTripDateRange(
          orgId, bucket.from, bucket.to, truckId, partyId));
      BigDecimal repairs = normalizeMoney(sumRepairCosts(orgId, bucket.from, bucket.to, truckId));
      BigDecimal tyres = normalizeMoney(sumTyreCosts(orgId, bucket.from, bucket.to, truckId));
      BigDecimal operating = fuel.add(toll).add(driver).add(repairs).add(tyres);

      OperatingSeriesPoint point = new OperatingSeriesPoint();
      point.setLabel(bucket.label);
      point.setDateFrom(bucket.from);
      point.setDateTo(bucket.to);
      point.setRevenueEarned(scaleMoney(revenue));
      point.setOperatingCost(scaleMoney(operating));
      point.setOperatingRatioPct(scalePercent(calcPercent(operating, revenue)));
      series.add(point);

      totalRevenue = totalRevenue.add(revenue);
      totalOperating = totalOperating.add(operating);
    }

    OperatingSummary summary = new OperatingSummary();
    summary.setRevenueEarned(scaleMoney(totalRevenue));
    summary.setOperatingCost(scaleMoney(totalOperating));
    summary.setOperatingRatioPct(scalePercent(calcPercent(totalOperating, totalRevenue)));
    summary.setTripCount(tripRepository.countCompletedTrips(orgId, from, to, truckId, partyId));

    OperatingVsRevenueReportResponse response = new OperatingVsRevenueReportResponse();
    response.setRange(new ReportRange(from, to, grouping));
    response.setSummary(summary);
    response.setSeries(series);
    response.setHighestCostTrips(buildHighestCostTrips(orgId, from, to, truckId, partyId));
    response.setHighestOverheadTrucks(buildHighestOverheadTrucks(orgId, from, to, truckId));
    return response;
  }

  private List<TopPartyRow> buildTopParties(UUID orgId, LocalDate from, LocalDate to, UUID truckId, UUID partyId) {
    List<PartyRevenueSummary> revenues = tripRepository.sumRevenueByParty(orgId, from, to, truckId, partyId);
    List<PartyReceivedSummary> received =
        settlementAllocationRepository.sumPaidByPartyForTripDateRange(orgId, from, to, truckId, partyId);

    Map<UUID, BigDecimal> receivedByParty = new HashMap<>();
    for (PartyReceivedSummary row : received) {
      receivedByParty.put(row.getPartyId(), normalizeMoney(row.getReceivedAmount()));
    }

    return revenues.stream()
        .map(row -> {
          BigDecimal revenue = normalizeMoney(row.getRevenueEarned());
          BigDecimal cash = receivedByParty.getOrDefault(row.getPartyId(), BigDecimal.ZERO);
          TopPartyRow dto = new TopPartyRow();
          dto.setPartyId(row.getPartyId());
          dto.setPartyName(row.getPartyName());
          dto.setRevenueEarned(scaleMoney(revenue));
          dto.setOutstanding(scaleMoney(revenue.subtract(cash)));
          return dto;
        })
        .sorted(Comparator.comparing(TopPartyRow::getRevenueEarned).reversed())
        .limit(5)
        .toList();
  }

  private List<TopTruckRow> buildTopTrucks(UUID orgId, LocalDate from, LocalDate to, UUID truckId, UUID partyId) {
    List<TruckRevenueSummary> revenues = tripRepository.sumRevenueByTruck(orgId, from, to, truckId, partyId);
    List<TruckTripCountSummary> counts = tripRepository.countTripsByTruck(orgId, from, to, truckId, partyId);

    Map<UUID, BigDecimal> expensesByTruck = new HashMap<>();
    mergeTruckExpense(expensesByTruck, fuelLogRepository.sumAmountByTruck(orgId, from, to, truckId, partyId));
    mergeTruckExpense(expensesByTruck, tollLogRepository.sumAmountByTruck(orgId, from, to, truckId, partyId));
    mergeTruckExpense(expensesByTruck, driverExpenseRepository.sumAmountByTruck(orgId, from, to, truckId, partyId));
    mergeTruckExpense(expensesByTruck, truckRepairRepository.sumByTruck(orgId, from, to));
    mergeTruckExpense(expensesByTruck, truckTyreExpenseRepository.sumByTruck(orgId, from, to));

    Map<UUID, Long> countByTruck = new HashMap<>();
    for (TruckTripCountSummary row : counts) {
      countByTruck.put(row.getTruckId(), row.getTripCount());
    }

    return revenues.stream()
        .map(row -> {
          BigDecimal revenue = normalizeMoney(row.getRevenueEarned());
          BigDecimal expenses = expensesByTruck.getOrDefault(row.getTruckId(), BigDecimal.ZERO);
          TopTruckRow dto = new TopTruckRow();
          dto.setTruckId(row.getTruckId());
          dto.setTruckNumber(row.getTruckNumber());
          dto.setRevenueEarned(scaleMoney(revenue));
          dto.setProfit(scaleMoney(revenue.subtract(expenses)));
          dto.setTripCount(countByTruck.getOrDefault(row.getTruckId(), 0L));
          return dto;
        })
        .sorted(Comparator.comparing(TopTruckRow::getRevenueEarned).reversed())
        .limit(5)
        .toList();
  }

  private List<HighestCostTripRow> buildHighestCostTrips(
      UUID orgId, LocalDate from, LocalDate to, UUID truckId, UUID partyId) {
    List<TripReportProjection> trips = tripRepository.findTripsForProfitBreakdown(orgId, from, to, truckId, partyId);
    if (trips.isEmpty()) {
      return List.of();
    }

    List<UUID> tripIds = trips.stream().map(TripReportProjection::getTripId).toList();
    Map<UUID, BigDecimal> fuelByTrip = fuelLogRepository
        .sumAmountByOrgIdAndTripIdInDateRange(orgId, tripIds, from, to).stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeMoney(sum.getTotalAmount())));
    Map<UUID, BigDecimal> tollByTrip = tollLogRepository
        .sumAmountByOrgIdAndTripIdInDateRange(orgId, tripIds, from, to).stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeMoney(sum.getTotalAmount())));
    Map<UUID, BigDecimal> driverByTrip = driverExpenseRepository
        .sumAmountByOrgIdAndTripIdInDateRange(orgId, tripIds, from, to).stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeMoney(sum.getTotalAmount())));

    return trips.stream()
        .map(trip -> {
          BigDecimal fuel = fuelByTrip.getOrDefault(trip.getTripId(), BigDecimal.ZERO);
          BigDecimal toll = tollByTrip.getOrDefault(trip.getTripId(), BigDecimal.ZERO);
          BigDecimal driver = driverByTrip.getOrDefault(trip.getTripId(), BigDecimal.ZERO);
          BigDecimal total = fuel.add(toll).add(driver);
          HighestCostTripRow row = new HighestCostTripRow();
          row.setTripId(trip.getTripId());
          row.setRoute(trip.getFromLocation() + " -> " + trip.getToLocation());
          row.setTruckNumber(trip.getTruckNumber());
          row.setFuel(scaleMoney(fuel));
          row.setTolls(scaleMoney(toll));
          row.setDriver(scaleMoney(driver));
          row.setTotalTripCost(scaleMoney(total));
          return row;
        })
        .sorted(Comparator.comparing(HighestCostTripRow::getTotalTripCost).reversed())
        .limit(5)
        .toList();
  }

  private List<HighestOverheadTruckRow> buildHighestOverheadTrucks(
      UUID orgId, LocalDate from, LocalDate to, UUID truckId) {
    Map<UUID, BigDecimal> repairsByTruck = new HashMap<>();
    mergeTruckExpense(repairsByTruck, truckRepairRepository.sumByTruck(orgId, from, to));
    Map<UUID, BigDecimal> tyresByTruck = new HashMap<>();
    mergeTruckExpense(tyresByTruck, truckTyreExpenseRepository.sumByTruck(orgId, from, to));

    Map<UUID, BigDecimal> overheadByTruck = new HashMap<>();
    for (UUID id : repairsByTruck.keySet()) {
      overheadByTruck.put(id, repairsByTruck.get(id));
    }
    for (Map.Entry<UUID, BigDecimal> entry : tyresByTruck.entrySet()) {
      overheadByTruck.merge(entry.getKey(), entry.getValue(), BigDecimal::add);
    }

    if (truckId != null) {
      BigDecimal overhead = overheadByTruck.get(truckId);
      if (overhead == null || overhead.signum() <= 0) {
        return List.of();
      }
      overheadByTruck.keySet().retainAll(List.of(truckId));
    }

    if (overheadByTruck.isEmpty()) {
      return List.of();
    }

    List<UUID> truckIds = new ArrayList<>(overheadByTruck.keySet());
    Map<UUID, String> truckNumberById = truckRepository.findAllByOrgIdAndIdIn(orgId, truckIds).stream()
        .collect(Collectors.toMap(
            Truck::getId,
            Truck::getTruckNumber));

    return overheadByTruck.entrySet().stream()
        .map(entry -> {
          UUID id = entry.getKey();
          BigDecimal repairs = repairsByTruck.getOrDefault(id, BigDecimal.ZERO);
          BigDecimal tyres = tyresByTruck.getOrDefault(id, BigDecimal.ZERO);
          BigDecimal overhead = entry.getValue();
          HighestOverheadTruckRow row = new HighestOverheadTruckRow();
          row.setTruckId(id);
          row.setTruckNumber(truckNumberById.get(id));
          row.setRepairs(scaleMoney(repairs));
          row.setTyres(scaleMoney(tyres));
          row.setOverhead(scaleMoney(overhead));
          return row;
        })
        .sorted(Comparator.comparing(HighestOverheadTruckRow::getOverhead).reversed())
        .limit(5)
        .toList();
  }

  private List<TripBreakdownRow> buildTripBreakdown(UUID orgId, LocalDate from, LocalDate to, UUID truckId, UUID partyId) {
    List<TripReportProjection> trips = tripRepository.findTripsForProfitBreakdown(orgId, from, to, truckId, partyId);
    if (trips.isEmpty()) {
      return List.of();
    }

    List<UUID> tripIds = trips.stream().map(TripReportProjection::getTripId).toList();
    Map<UUID, BigDecimal> fuelByTrip = fuelLogRepository
        .sumAmountByOrgIdAndTripIdInDateRange(orgId, tripIds, from, to).stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeMoney(sum.getTotalAmount())));
    Map<UUID, BigDecimal> tollByTrip = tollLogRepository
        .sumAmountByOrgIdAndTripIdInDateRange(orgId, tripIds, from, to).stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeMoney(sum.getTotalAmount())));
    Map<UUID, BigDecimal> driverByTrip = driverExpenseRepository
        .sumAmountByOrgIdAndTripIdInDateRange(orgId, tripIds, from, to).stream()
        .collect(Collectors.toMap(TripAmountSum::getTripId, sum -> normalizeMoney(sum.getTotalAmount())));

    List<TripBreakdownRow> rows = new ArrayList<>();
    for (TripReportProjection trip : trips) {
      BigDecimal revenue = normalizeMoney(trip.getFreightAmount());
      BigDecimal fuel = fuelByTrip.getOrDefault(trip.getTripId(), BigDecimal.ZERO);
      BigDecimal toll = tollByTrip.getOrDefault(trip.getTripId(), BigDecimal.ZERO);
      BigDecimal driver = driverByTrip.getOrDefault(trip.getTripId(), BigDecimal.ZERO);
      BigDecimal costs = fuel.add(toll).add(driver);

      TripBreakdownRow row = new TripBreakdownRow();
      row.setTripId(trip.getTripId());
      row.setTripCode(trip.getTripCode());
      row.setFromLocation(trip.getFromLocation());
      row.setToLocation(trip.getToLocation());
      row.setTruckId(trip.getTruckId());
      row.setTruckNumber(trip.getTruckNumber());
      row.setRevenueEarned(scaleMoney(revenue));
      row.setFuel(scaleMoney(fuel));
      row.setTolls(scaleMoney(toll));
      row.setDriver(scaleMoney(driver));
      row.setDirectProfit(scaleMoney(revenue.subtract(costs)));
      row.setStatus(trip.getStatus());
      row.setStartDate(trip.getStartDate());
      rows.add(row);
    }
    return rows;
  }

  private List<TruckSummaryRow> buildTruckSummary(UUID orgId, LocalDate from, LocalDate to, List<TripBreakdownRow> tripBreakdown) {
    if (tripBreakdown == null || tripBreakdown.isEmpty()) {
      return List.of();
    }

    Map<UUID, BigDecimal> repairsByTruck = new HashMap<>();
    for (TruckExpenseSummary row : truckRepairRepository.sumByTruck(orgId, from, to)) {
      repairsByTruck.put(row.getTruckId(), normalizeMoney(row.getTotalAmount()));
    }

    Map<UUID, BigDecimal> tyresByTruck = new HashMap<>();
    for (TruckExpenseSummary row : truckTyreExpenseRepository.sumByTruck(orgId, from, to)) {
      tyresByTruck.put(row.getTruckId(), normalizeMoney(row.getTotalAmount()));
    }

    Map<UUID, TruckSummaryRow> summaryByTruck = new HashMap<>();
    for (TripBreakdownRow trip : tripBreakdown) {
      if (trip.getTruckId() == null) {
        continue;
      }
      TruckSummaryRow row = summaryByTruck.computeIfAbsent(trip.getTruckId(), key -> {
        TruckSummaryRow dto = new TruckSummaryRow();
        dto.setTruckId(trip.getTruckId());
        dto.setTruckNumber(trip.getTruckNumber());
        dto.setTrips(0);
        dto.setRevenueEarned(BigDecimal.ZERO);
        dto.setTripCosts(BigDecimal.ZERO);
        return dto;
      });

      row.setTrips(row.getTrips() + 1);
      row.setRevenueEarned(row.getRevenueEarned().add(normalizeMoney(trip.getRevenueEarned())));
      BigDecimal tripCosts = normalizeMoney(trip.getFuel())
          .add(normalizeMoney(trip.getTolls()))
          .add(normalizeMoney(trip.getDriver()));
      row.setTripCosts(row.getTripCosts().add(tripCosts));
    }

    List<TruckSummaryRow> rows = new ArrayList<>();
    for (TruckSummaryRow row : summaryByTruck.values()) {
      BigDecimal repairs = repairsByTruck.getOrDefault(row.getTruckId(), BigDecimal.ZERO);
      BigDecimal tyres = tyresByTruck.getOrDefault(row.getTruckId(), BigDecimal.ZERO);
      BigDecimal overhead = repairs.add(tyres);
      BigDecimal directProfit = row.getRevenueEarned().subtract(row.getTripCosts());
      BigDecimal netProfit = row.getRevenueEarned().subtract(row.getTripCosts().add(overhead));

      row.setRevenueEarned(scaleMoney(row.getRevenueEarned()));
      row.setTripCosts(scaleMoney(row.getTripCosts()));
      row.setDirectProfit(scaleMoney(directProfit));
      row.setRepairs(scaleMoney(repairs));
      row.setTyres(scaleMoney(tyres));
      row.setOverhead(scaleMoney(overhead));
      row.setNetProfit(scaleMoney(netProfit));

      rows.add(row);
    }

    rows.sort(Comparator.comparing(TruckSummaryRow::getRevenueEarned).reversed());
    return rows;
  }

  private void mergeTruckExpense(Map<UUID, BigDecimal> target, List<TruckExpenseSummary> rows) {
    for (TruckExpenseSummary row : rows) {
      BigDecimal current = target.getOrDefault(row.getTruckId(), BigDecimal.ZERO);
      target.put(row.getTruckId(), current.add(normalizeMoney(row.getTotalAmount())));
    }
  }

  private BigDecimal sumRepairCosts(UUID orgId, LocalDate from, LocalDate to, UUID truckId) {
    if (truckId == null) {
      return truckRepairRepository.sumByOrgIdAndDateRange(orgId, from, to);
    }
    return truckRepairRepository.sumByOrgIdTruckIdAndDateRange(orgId, truckId, from, to);
  }

  private BigDecimal sumTyreCosts(UUID orgId, LocalDate from, LocalDate to, UUID truckId) {
    if (truckId == null) {
      return truckTyreExpenseRepository.sumByOrgIdAndDateRange(orgId, from, to);
    }
    return truckTyreExpenseRepository.sumByOrgIdTruckIdAndDateRange(orgId, truckId, from, to);
  }

  private void validateRange(LocalDate from, LocalDate to) {
    if (from == null || to == null) {
      throw new BadRequestException("from and to are required");
    }
    if (from.isAfter(to)) {
      throw new BadRequestException("from must be on or before to");
    }
  }

  private String normalizeGroupBy(String groupBy) {
    if (groupBy == null || groupBy.isBlank()) {
      return "month";
    }
    String normalized = groupBy.trim().toLowerCase();
    if (!normalized.equals("day") && !normalized.equals("week") && !normalized.equals("month")) {
      throw new BadRequestException("groupBy must be day, week, or month");
    }
    return normalized;
  }

  private List<Bucket> buildBuckets(LocalDate from, LocalDate to, String groupBy) {
    List<Bucket> buckets = new ArrayList<>();
    if (groupBy.equals("day")) {
      LocalDate cursor = from;
      while (!cursor.isAfter(to)) {
        buckets.add(new Bucket(cursor, cursor, cursor.format(LABEL_DAY)));
        cursor = cursor.plusDays(1);
      }
      return buckets;
    }
    if (groupBy.equals("week")) {
      LocalDate cursor = from;
      while (!cursor.isAfter(to)) {
        LocalDate weekEnd = cursor.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        if (weekEnd.isAfter(to)) {
          weekEnd = to;
        }
        String label = cursor.format(LABEL_DAY) + " - " + weekEnd.format(LABEL_DAY);
        buckets.add(new Bucket(cursor, weekEnd, label));
        cursor = weekEnd.plusDays(1);
      }
      return buckets;
    }
    LocalDate cursor = from.withDayOfMonth(1);
    if (cursor.isBefore(from)) {
      cursor = from;
    }
    while (!cursor.isAfter(to)) {
      YearMonth ym = YearMonth.from(cursor);
      LocalDate monthEnd = ym.atEndOfMonth();
      LocalDate bucketStart = cursor;
      if (bucketStart.isBefore(from)) {
        bucketStart = from;
      }
      LocalDate bucketEnd = monthEnd.isAfter(to) ? to : monthEnd;
      buckets.add(new Bucket(bucketStart, bucketEnd, ym.format(LABEL_MONTH)));
      cursor = bucketEnd.plusDays(1);
    }
    return buckets;
  }

  private BigDecimal normalizeMoney(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private BigDecimal scaleMoney(BigDecimal value) {
    return normalizeMoney(value).setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal calcPercent(BigDecimal numerator, BigDecimal denominator) {
    if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    return numerator.multiply(new BigDecimal("100")).divide(denominator, 4, RoundingMode.HALF_UP);
  }

  private BigDecimal scalePercent(BigDecimal value) {
    return normalizeMoney(value).setScale(2, RoundingMode.HALF_UP);
  }

  private static class Bucket {
    private final LocalDate from;
    private final LocalDate to;
    private final String label;

    private Bucket(LocalDate from, LocalDate to, String label) {
      this.from = from;
      this.to = to;
      this.label = label;
    }
  }
}
