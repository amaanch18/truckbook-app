package com.truckbook.api.repository;

import com.truckbook.api.entity.TruckTyreExpense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TruckTyreExpenseRepository extends JpaRepository<TruckTyreExpense, UUID> {
  List<TruckTyreExpense> findAllByOrgIdAndTruckIdOrderByEntryDateDesc(UUID orgId, UUID truckId);
  List<TruckTyreExpense> findAllByOrgIdAndTruckIdAndEntryDateBetweenOrderByEntryDateDesc(
      UUID orgId,
      UUID truckId,
      LocalDate from,
      LocalDate to);
  Optional<TruckTyreExpense> findByOrgIdAndTruckIdAndId(UUID orgId, UUID truckId, UUID id);

  @Query("select coalesce(sum(t.amount),0) from TruckTyreExpense t where t.orgId=:orgId and t.entryDate between :from and :to")
  BigDecimal sumByOrgIdAndDateRange(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select coalesce(sum(t.amount),0) from TruckTyreExpense t where t.orgId=:orgId and t.truckId=:truckId and t.entryDate between :from and :to")
  BigDecimal sumByOrgIdTruckIdAndDateRange(
      @Param("orgId") UUID orgId,
      @Param("truckId") UUID truckId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);

  @Query("select t.truckId as truckId, sum(t.amount) as totalAmount from TruckTyreExpense t "
      + "where t.orgId=:orgId and t.entryDate between :from and :to group by t.truckId")
  List<TruckExpenseSummary> sumByTruck(
      @Param("orgId") UUID orgId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to);
}
