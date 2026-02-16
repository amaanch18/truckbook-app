-- V1__init_truckbook.sql
-- TruckBook schema (PostgreSQL) - multi-tenant by org_id

BEGIN;

-- Extensions (optional but useful)
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- gen_random_uuid()

-- =========================
-- 1) Core: orgs + users + auth (OTP)
-- =========================

CREATE TABLE organizations (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name          TEXT NOT NULL,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  phone_e164    TEXT NOT NULL, -- +9199xxxx...
  display_name  TEXT,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (org_id, phone_e164)
);

-- OTP requests: store hashed code (never store plain OTP)
CREATE TABLE otp_requests (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id          UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  phone_e164      TEXT NOT NULL,
  otp_hash        TEXT NOT NULL,
  expires_at      TIMESTAMPTZ NOT NULL,
  consumed_at     TIMESTAMPTZ,
  attempt_count   INT NOT NULL DEFAULT 0,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_otp_org_phone_created ON otp_requests(org_id, phone_e164, created_at DESC);

-- =========================
-- 2) Master data: parties + trucks
-- =========================

CREATE TABLE parties (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  name          TEXT NOT NULL,
  phone         TEXT,
  notes         TEXT,
  is_active     BOOLEAN NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (org_id, name)
);

CREATE TABLE trucks (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  truck_number  TEXT NOT NULL,      -- "MH 01 AB 1880"
  truck_type    TEXT,               -- "Container", "Tanker", "Trailer" (string for now)
  status        TEXT NOT NULL DEFAULT 'ACTIVE', -- ACTIVE/INACTIVE
  notes         TEXT,
  -- Documents/compliance (simple fields for MVP; can normalize later)
  insurance_status   TEXT DEFAULT 'MISSING', -- MISSING/AVAILABLE
  insurance_expiry   DATE,
  permit_status      TEXT DEFAULT 'MISSING',
  permit_expiry      DATE,
  fitness_status     TEXT DEFAULT 'MISSING',
  fitness_expiry     DATE,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (org_id, truck_number)
);

CREATE INDEX idx_trucks_org_status ON trucks(org_id, status);

-- =========================
-- 3) Trips: the core entity
-- =========================

CREATE TABLE trips (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id          UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,

  trip_code       TEXT NOT NULL, -- UI-friendly: TRP-564367 (generate in app)
  status          TEXT NOT NULL DEFAULT 'ACTIVE', -- ACTIVE/COMPLETED

  party_id        UUID REFERENCES parties(id) ON DELETE SET NULL,
  truck_id        UUID REFERENCES trucks(id) ON DELETE SET NULL,

  driver_name     TEXT,

  from_location   TEXT NOT NULL,
  to_location     TEXT NOT NULL,
  start_date      DATE NOT NULL,

  notes           TEXT,

  -- Revenue (accrual): freight agreed/charged
  freight_amount  NUMERIC(12,2) NOT NULL DEFAULT 0,

  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

  UNIQUE (org_id, trip_code)
);

CREATE INDEX idx_trips_org_status_date ON trips(org_id, status, start_date DESC);
CREATE INDEX idx_trips_org_truck_date ON trips(org_id, truck_id, start_date DESC);
CREATE INDEX idx_trips_org_party_date ON trips(org_id, party_id, start_date DESC);

-- =========================
-- 4) Trip logs: fuel + toll + driver expenses
-- =========================

CREATE TABLE fuel_logs (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  trip_id       UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
  entry_date    DATE NOT NULL,
  liters        NUMERIC(10,2),
  amount        NUMERIC(12,2) NOT NULL DEFAULT 0,
  odometer_km   NUMERIC(12,2),
  notes         TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_fuel_trip_date ON fuel_logs(org_id, trip_id, entry_date DESC);

CREATE TABLE toll_logs (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  trip_id       UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
  entry_date    DATE NOT NULL,
  plaza         TEXT,
  amount        NUMERIC(12,2) NOT NULL DEFAULT 0,
  notes         TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_toll_trip_date ON toll_logs(org_id, trip_id, entry_date DESC);

-- New: driver expenses inside trip details (food, stay, misc)
CREATE TABLE driver_expenses (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  trip_id       UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
  entry_date    DATE NOT NULL,
  category      TEXT NOT NULL, -- FOOD/STAY/MISC (string for MVP)
  amount        NUMERIC(12,2) NOT NULL DEFAULT 0,
  notes         TEXT,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_driver_exp_trip_date ON driver_expenses(org_id, trip_id, entry_date DESC);

-- =========================
-- 5) Settlements (party-level allowed) + allocations to trips
-- =========================

-- Settlements are cash/received money from party.
-- IMPORTANT: truck_id is nullable to support party-level lump sum settlements.
CREATE TABLE settlements (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id          UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,

  settlement_code TEXT NOT NULL, -- UI: SET-1769016074402 (generate in app)

  party_id        UUID NOT NULL REFERENCES parties(id) ON DELETE RESTRICT,
  truck_id        UUID NULL REFERENCES trucks(id) ON DELETE SET NULL, -- nullable (party-level settlement)

  settlement_date DATE NOT NULL,
  received_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  mode            TEXT NOT NULL DEFAULT 'CASH', -- CASH/UPI/BANK etc.
  reference       TEXT,
  notes           TEXT,

  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

  UNIQUE (org_id, settlement_code)
);

CREATE INDEX idx_settlements_org_date ON settlements(org_id, settlement_date DESC);
CREATE INDEX idx_settlements_org_party_date ON settlements(org_id, party_id, settlement_date DESC);
CREATE INDEX idx_settlements_org_truck_date ON settlements(org_id, truck_id, settlement_date DESC);

-- Allocation: how settlement money is applied to trips
CREATE TABLE settlement_allocations (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id          UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,

  settlement_id   UUID NOT NULL REFERENCES settlements(id) ON DELETE CASCADE,
  trip_id         UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,

  allocated_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

  -- Prevent duplicate allocations of same settlement to same trip
  UNIQUE (org_id, settlement_id, trip_id)
);

CREATE INDEX idx_alloc_settlement ON settlement_allocations(org_id, settlement_id);
CREATE INDEX idx_alloc_trip ON settlement_allocations(org_id, trip_id);

-- =========================
-- 6) Truck-level costs (repairs + tyres)
-- =========================

-- New: repairs/maintenance costs (used later for truck-wise profit)
CREATE TABLE truck_repairs (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  truck_id      UUID NOT NULL REFERENCES trucks(id) ON DELETE CASCADE,
  entry_date    DATE NOT NULL,
  vendor        TEXT,
  description   TEXT,
  amount        NUMERIC(12,2) NOT NULL DEFAULT 0,
  odometer_km   NUMERIC(12,2),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_repairs_truck_date ON truck_repairs(org_id, truck_id, entry_date DESC);

-- New: tyre costs (purchase/replace/repair)
CREATE TABLE tyre_expenses (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id        UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
  truck_id      UUID NOT NULL REFERENCES trucks(id) ON DELETE CASCADE,
  entry_date    DATE NOT NULL,
  tyre_position TEXT,           -- e.g. FRONT_LEFT, etc (string for MVP)
  description   TEXT,
  amount        NUMERIC(12,2) NOT NULL DEFAULT 0,
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tyre_truck_date ON tyre_expenses(org_id, truck_id, entry_date DESC);

COMMIT;
