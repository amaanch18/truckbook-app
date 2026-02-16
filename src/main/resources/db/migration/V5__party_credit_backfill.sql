UPDATE truckbook.parties p
SET credit_amount = COALESCE(s.total_unallocated, 0)
FROM (
  SELECT party_id, SUM(unallocated_amount) AS total_unallocated
  FROM truckbook.settlements
  GROUP BY party_id
) s
WHERE p.id = s.party_id;
