ALTER TABLE truckbook.parties
ADD COLUMN credit_amount NUMERIC(12,2) NOT NULL DEFAULT 0;

ALTER TABLE truckbook.settlements
ADD COLUMN unallocated_amount NUMERIC(12,2) NOT NULL DEFAULT 0;

UPDATE truckbook.settlements
SET unallocated_amount = received_amount;

UPDATE truckbook.settlements s
SET unallocated_amount = s.received_amount - COALESCE(a.allocated, 0)
FROM (
  SELECT settlement_id, SUM(allocated_amount) AS allocated
  FROM truckbook.settlement_allocations
  GROUP BY settlement_id
) a
WHERE s.id = a.settlement_id;

UPDATE truckbook.settlements
SET unallocated_amount = 0
WHERE unallocated_amount < 0;
