ALTER TABLE truckbook.organizations
  ADD COLUMN IF NOT EXISTS onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE truckbook.organizations
  ADD COLUMN IF NOT EXISTS city VARCHAR(100);

ALTER TABLE truckbook.organizations
  ADD COLUMN IF NOT EXISTS owner_display_name VARCHAR(120);
