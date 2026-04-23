-- Remove aggregation_method from condition table
ALTER TABLE condition DROP COLUMN IF EXISTS aggregation_method;
