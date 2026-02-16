ALTER TABLE truckbook.otp_requests
  ALTER COLUMN otp_hash DROP NOT NULL;

ALTER TABLE truckbook.otp_requests
  ADD COLUMN last_sent_at TIMESTAMPTZ NULL,
  ADD COLUMN send_count INT NOT NULL DEFAULT 0,
  ADD COLUMN provider TEXT NULL,
  ADD COLUMN provider_ref TEXT NULL;
