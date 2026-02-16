-- V2__views_reports.sql
-- Reporting views for TruckBook (PostgreSQL)
-- NOTE: These are normal VIEWs (not materialized). They always reflect latest data.

BEGIN;

-- ============================================================
-- 1) Trip-level cost breakdown (Fuel / Toll / Driver)
-- ============================================================

CREATE OR REPLACE VIEW vw_trip_costs AS
SELECT
  t.org_id,
  t.id AS trip_id,
  COALESCE(f.fuel_cost, 0)::numeric(12,2)   AS fuel_cost,
  COALESCE(to1.toll_cost, 0)::numeric(12,2) AS toll_cost,
  COALESCE(d.driver_cost, 0)::numeric(12,2) AS driver_cost,
  (
    COALESCE(f.fuel_cost, 0) +
    COALESCE(to1.toll_cost, 0) +
    COALESCE(d.driver_cost, 0)
  )::numeric(12,2) AS trip_cost_total
FROM trips t
LEFT JOIN (
  SELECT org_id, trip_id, SUM(amount) AS fuel_cost
  FROM fuel_logs
  GROUP BY org_id, trip_id
) f ON f.org_id = t.org_id AND f.trip_id = t.id
LEFT JOIN (
  SELECT org_id, trip_id, SUM(amount) AS toll_cost
  FROM toll_logs
  GROUP BY org_id, trip_id
) to1 ON to1.org_id = t.org_id AND to1.trip_id = t.id
LEFT JOIN (
  SELECT org_id, trip_id, SUM(amount) AS driver_cost
  FROM driver_expenses
  GROUP BY org_id, trip_id
) d ON d.org_id = t.org_id AND d.trip_id = t.id;


-- ============================================================
-- 2) Trip-level cash received (from settlement allocations)
--    (cash is attributed to settlement_date)
-- ============================================================

CREATE OR REPLACE VIEW vw_trip_cash_received AS
SELECT
  s.org_id,
  sa.trip_id,
  SUM(sa.allocated_amount)::numeric(12,2) AS cash_received_total
FROM settlement_allocations sa
JOIN settlements s
  ON s.id = sa.settlement_id
 AND s.org_id = sa.org_id
GROUP BY s.org_id, sa.trip_id;


-- ============================================================
-- 3) Trip financials (for Trip Details, Trip list pills, Outstanding)
-- ============================================================

CREATE OR REPLACE VIEW vw_trip_financials AS
SELECT
  t.org_id,
  t.id AS trip_id,
  t.trip_code,
  t.status,
  t.start_date,
  t.party_id,
  p.name AS party_name,
  t.truck_id,
  tr.truck_number,
  t.from_location,
  t.to_location,
  t.freight_amount::numeric(12,2) AS revenue_accrual,

  COALESCE(tc.fuel_cost, 0)::numeric(12,2)   AS fuel_cost,
  COALESCE(tc.toll_cost, 0)::numeric(12,2)   AS toll_cost,
  COALESCE(tc.driver_cost, 0)::numeric(12,2) AS driver_cost,
  COALESCE(tc.trip_cost_total, 0)::numeric(12,2) AS trip_cost_total,

  COALESCE(cr.cash_received_total, 0)::numeric(12,2) AS cash_received,

  (t.freight_amount - COALESCE(cr.cash_received_total, 0))::numeric(12,2) AS pending_settlement,

  (t.freight_amount - COALESCE(tc.trip_cost_total, 0))::numeric(12,2) AS direct_profit_accrual
FROM trips t
LEFT JOIN parties p
  ON p.id = t.party_id AND p.org_id = t.org_id
LEFT JOIN trucks tr
  ON tr.id = t.truck_id AND tr.org_id = t.org_id
LEFT JOIN vw_trip_costs tc
  ON tc.trip_id = t.id AND tc.org_id = t.org_id
LEFT JOIN vw_trip_cash_received cr
  ON cr.trip_id = t.id AND cr.org_id = t.org_id;


-- ============================================================
-- 4) Truck overhead (Repairs + Tyres) - aggregated by truck
-- ============================================================

CREATE OR REPLACE VIEW vw_truck_overhead AS
SELECT
  x.org_id,
  x.truck_id,
  SUM(x.repairs_cost)::numeric(12,2) AS repairs_cost,
  SUM(x.tyres_cost)::numeric(12,2)   AS tyres_cost,
  (SUM(x.repairs_cost) + SUM(x.tyres_cost))::numeric(12,2) AS overhead_total
FROM (
  SELECT org_id, truck_id, SUM(amount) AS repairs_cost, 0::numeric AS tyres_cost
  FROM truck_repairs
  GROUP BY org_id, truck_id

  UNION ALL

  SELECT org_id, truck_id, 0::numeric AS repairs_cost, SUM(amount) AS tyres_cost
  FROM tyre_expenses
  GROUP BY org_id, truck_id
) x
GROUP BY x.org_id, x.truck_id;


-- ============================================================
-- 5) Truck profit summary (accrual) - good for Profit tab truck table
--    NOTE: revenue & trip costs are based on trips.start_date filters in app queries.
--    This view gives per-truck totals across ALL time; filter by start_date in queries.
-- ============================================================

CREATE OR REPLACE VIEW vw_truck_profit_summary AS
SELECT
  t.org_id,
  t.truck_id,
  tr.truck_number,

  COUNT(*)::int AS trips_count,
  SUM(t.revenue_accrual)::numeric(12,2) AS revenue_accrual,
  SUM(t.trip_cost_total)::numeric(12,2) AS trip_cost_total,
  SUM(t.direct_profit_accrual)::numeric(12,2) AS direct_profit_accrual,

  COALESCE(o.repairs_cost, 0)::numeric(12,2) AS repairs_cost_all_time,
  COALESCE(o.tyres_cost, 0)::numeric(12,2)   AS tyres_cost_all_time,
  COALESCE(o.overhead_total, 0)::numeric(12,2) AS overhead_total_all_time,

  (SUM(t.direct_profit_accrual) - COALESCE(o.overhead_total, 0))::numeric(12,2) AS net_profit_all_time
FROM vw_trip_financials t
LEFT JOIN trucks tr
  ON tr.id = t.truck_id AND tr.org_id = t.org_id
LEFT JOIN vw_truck_overhead o
  ON o.truck_id = t.truck_id AND o.org_id = t.org_id
GROUP BY
  t.org_id, t.truck_id, tr.truck_number,
  o.repairs_cost, o.tyres_cost, o.overhead_total;


-- ============================================================
-- 6) Daily report metrics (for Overview / Profit trend / Ops vs Revenue)
--    This produces one row per org per date with cost breakdown.
--
--    Date attribution rules:
--    - Revenue accrual uses trips.start_date
--    - Fuel/Toll/Driver use their entry_date
--    - Repairs/Tyres use their entry_date
--    - Cash received uses settlements.settlement_date
--
--    UI can group daily -> weekly/monthly in backend queries.
-- ============================================================

CREATE OR REPLACE VIEW vw_report_daily_metrics AS
WITH
revenue_daily AS (
  SELECT
    org_id,
    start_date::date AS metric_date,
    SUM(freight_amount)::numeric(12,2) AS revenue_accrual
  FROM trips
  GROUP BY org_id, start_date::date
),
fuel_daily AS (
  SELECT
    org_id,
    entry_date::date AS metric_date,
    SUM(amount)::numeric(12,2) AS fuel_cost
  FROM fuel_logs
  GROUP BY org_id, entry_date::date
),
toll_daily AS (
  SELECT
    org_id,
    entry_date::date AS metric_date,
    SUM(amount)::numeric(12,2) AS toll_cost
  FROM toll_logs
  GROUP BY org_id, entry_date::date
),
driver_daily AS (
  SELECT
    org_id,
    entry_date::date AS metric_date,
    SUM(amount)::numeric(12,2) AS driver_cost
  FROM driver_expenses
  GROUP BY org_id, entry_date::date
),
repairs_daily AS (
  SELECT
    org_id,
    entry_date::date AS metric_date,
    SUM(amount)::numeric(12,2) AS repairs_cost
  FROM truck_repairs
  GROUP BY org_id, entry_date::date
),
tyres_daily AS (
  SELECT
    org_id,
    entry_date::date AS metric_date,
    SUM(amount)::numeric(12,2) AS tyres_cost
  FROM tyre_expenses
  GROUP BY org_id, entry_date::date
),
cash_daily AS (
  SELECT
    s.org_id,
    s.settlement_date::date AS metric_date,
    SUM(sa.allocated_amount)::numeric(12,2) AS cash_received
  FROM settlement_allocations sa
  JOIN settlements s
    ON s.id = sa.settlement_id
   AND s.org_id = sa.org_id
  GROUP BY s.org_id, s.settlement_date::date
),
all_dates AS (
  SELECT org_id, metric_date FROM revenue_daily
  UNION
  SELECT org_id, metric_date FROM fuel_daily
  UNION
  SELECT org_id, metric_date FROM toll_daily
  UNION
  SELECT org_id, metric_date FROM driver_daily
  UNION
  SELECT org_id, metric_date FROM repairs_daily
  UNION
  SELECT org_id, metric_date FROM tyres_daily
  UNION
  SELECT org_id, metric_date FROM cash_daily
)
SELECT
  d.org_id,
  d.metric_date,

  COALESCE(r.revenue_accrual, 0)::numeric(12,2) AS revenue_accrual,
  COALESCE(c.cash_received, 0)::numeric(12,2)   AS cash_received,

  COALESCE(f.fuel_cost, 0)::numeric(12,2)       AS fuel_cost,
  COALESCE(tl.toll_cost, 0)::numeric(12,2)      AS toll_cost,
  COALESCE(dr.driver_cost, 0)::numeric(12,2)    AS driver_cost,

  (COALESCE(f.fuel_cost,0) + COALESCE(tl.toll_cost,0) + COALESCE(dr.driver_cost,0))::numeric(12,2)
    AS trip_cost_total,

  COALESCE(rp.repairs_cost, 0)::numeric(12,2)   AS repairs_cost,
  COALESCE(ty.tyres_cost, 0)::numeric(12,2)     AS tyres_cost,

  (COALESCE(rp.repairs_cost,0) + COALESCE(ty.tyres_cost,0))::numeric(12,2)
    AS overhead_total,

  (COALESCE(r.revenue_accrual,0) - (COALESCE(f.fuel_cost,0) + COALESCE(tl.toll_cost,0) + COALESCE(dr.driver_cost,0)))::numeric(12,2)
    AS direct_profit_accrual,

  (COALESCE(r.revenue_accrual,0) - (COALESCE(f.fuel_cost,0) + COALESCE(tl.toll_cost,0) + COALESCE(dr.driver_cost,0) + COALESCE(rp.repairs_cost,0) + COALESCE(ty.tyres_cost,0)))::numeric(12,2)
    AS net_profit_accrual

FROM all_dates d
LEFT JOIN revenue_daily r ON r.org_id = d.org_id AND r.metric_date = d.metric_date
LEFT JOIN cash_daily c    ON c.org_id = d.org_id AND c.metric_date = d.metric_date
LEFT JOIN fuel_daily f    ON f.org_id = d.org_id AND f.metric_date = d.metric_date
LEFT JOIN toll_daily tl   ON tl.org_id = d.org_id AND tl.metric_date = d.metric_date
LEFT JOIN driver_daily dr ON dr.org_id = d.org_id AND dr.metric_date = d.metric_date
LEFT JOIN repairs_daily rp ON rp.org_id = d.org_id AND rp.metric_date = d.metric_date
LEFT JOIN tyres_daily ty   ON ty.org_id = d.org_id AND ty.metric_date = d.metric_date;


-- ============================================================
-- 7) Outstanding (by party and by truck) using trip financials
--    Useful for your "Outstanding" cards in Settlements page
-- ============================================================

CREATE OR REPLACE VIEW vw_outstanding_by_party AS
SELECT
  org_id,
  party_id,
  party_name,
  SUM(pending_settlement)::numeric(12,2) AS outstanding_amount
FROM vw_trip_financials
WHERE party_id IS NOT NULL
GROUP BY org_id, party_id, party_name
HAVING SUM(pending_settlement) > 0;

CREATE OR REPLACE VIEW vw_outstanding_by_truck AS
SELECT
  org_id,
  truck_id,
  truck_number,
  SUM(pending_settlement)::numeric(12,2) AS outstanding_amount
FROM vw_trip_financials
WHERE truck_id IS NOT NULL
GROUP BY org_id, truck_id, truck_number
HAVING SUM(pending_settlement) > 0;

COMMIT;
