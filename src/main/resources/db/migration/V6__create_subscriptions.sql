CREATE TABLE truckbook.subscriptions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  org_id UUID NOT NULL REFERENCES truckbook.organizations(id),
  plan_code TEXT NOT NULL,
  status TEXT NOT NULL,
  trial_ends_at TIMESTAMPTZ,
  current_period_start TIMESTAMPTZ,
  current_period_end TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT subscriptions_plan_check
      CHECK (plan_code IN ('STARTER','GROWTH','PRO')),
  CONSTRAINT subscriptions_status_check
      CHECK (status IN ('TRIAL','ACTIVE','EXPIRED'))
);

CREATE UNIQUE INDEX ux_subscriptions_org
ON truckbook.subscriptions(org_id);
