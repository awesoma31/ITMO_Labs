-- =====================================================
-- ONE-TIME CLEANUP SCRIPT FOR EXISTING OLD DATA
-- =====================================================
-- Run this ONCE after applying V11 migration
-- This will delete old data that accumulated before retention policies
-- =====================================================

-- IMPORTANT: Run this inside the TimescaleDB container:
-- sudo docker exec -it cryptoterm_timescaledb psql -U cryptoterm -d cryptoterm -f /path/to/this/file.sql

BEGIN;

-- Show current sizes BEFORE cleanup
SELECT 
    'metric' as table_name,
    pg_size_pretty(pg_total_relation_size('metric')) as total_size,
    count(*) as row_count,
    min(time) as oldest,
    max(time) as newest
FROM metric
UNION ALL
SELECT 
    'device_log',
    pg_size_pretty(pg_total_relation_size('device_log')),
    count(*),
    min(time),
    max(time)
FROM device_log;

-- Delete old metrics (keep only last 1 day)
DELETE FROM metric 
WHERE time < now() - INTERVAL '1 day';

-- Delete old logs (keep only last 3 days)
DELETE FROM device_log 
WHERE time < now() - INTERVAL '3 days';

-- Vacuum to reclaim disk space
VACUUM FULL metric;
VACUUM FULL device_log;

-- Show sizes AFTER cleanup
SELECT 
    'metric' as table_name,
    pg_size_pretty(pg_total_relation_size('metric')) as total_size,
    count(*) as row_count,
    min(time) as oldest,
    max(time) as newest
FROM metric
UNION ALL
SELECT 
    'device_log',
    pg_size_pretty(pg_total_relation_size('device_log')),
    count(*),
    min(time),
    max(time)
FROM device_log;

COMMIT;

-- Show all retention policies
SELECT * FROM timescaledb_information.jobs 
WHERE proc_name IN ('policy_retention', 'policy_compression', 'policy_refresh_continuous_aggregate');
