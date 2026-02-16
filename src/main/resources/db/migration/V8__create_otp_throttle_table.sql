CREATE TABLE truckbook.otp_throttle (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  phone_e164 TEXT NOT NULL,
  sent_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  provider TEXT NULL
);

CREATE INDEX idx_otp_throttle_phone_sent_at
  ON truckbook.otp_throttle (phone_e164, sent_at DESC);
