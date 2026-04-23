-- =====================================================
-- FIX FAILED MIGRATION V11
-- =====================================================
-- This script manually applies V11 and fixes Flyway state
-- Run this AFTER updating the V11 file with WITH NO DATA
-- =====================================================

BEGIN;

-- 1. Delete failed migration record from Flyway
DELETE FROM flyway_schema_history 
WHERE version = '11' AND success = false;

COMMIT;

-- Now the corrected V11 migration will be applied automatically on next backend restart
-- Or you can apply it manually below:

-- =====================================================
-- MANUAL APPLICATION OF V11 (if needed)
-- =====================================================

-- METRICS: Retention policy (can run in transaction)
DO $$ 
BEGIN
    -- Check if policy already exists
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_retention' 
        AND hypertable_name = 'metric'
    ) THEN
        PERFORM add_retention_policy('metric', INTERVAL '1 day');
    END IF;
END $$;

-- DEVICE_LOG: Retention policy (can run in transaction)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_retention' 
        AND hypertable_name = 'device_log'
    ) THEN
        PERFORM add_retention_policy('device_log', INTERVAL '3 days');
    END IF;
END $$;

-- Enable compression
DO $$
BEGIN
    -- Check if metric compression is already enabled
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.compression_settings
        WHERE hypertable_name = 'metric'
    ) THEN
        ALTER TABLE metric SET (
            timescaledb.compress,
            timescaledb.compress_segmentby = 'device_id, miner_id'
        );
    END IF;
    
    -- Check if device_log compression is already enabled
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.compression_settings
        WHERE hypertable_name = 'device_log'
    ) THEN
        ALTER TABLE device_log SET (
            timescaledb.compress,
            timescaledb.compress_segmentby = 'device_id, level'
        );
    END IF;
END $$;

-- Compression policies
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_compression' 
        AND hypertable_name = 'metric'
    ) THEN
        PERFORM add_compression_policy('metric', INTERVAL '6 hours');
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_compression' 
        AND hypertable_name = 'device_log'
    ) THEN
        PERFORM add_compression_policy('device_log', INTERVAL '6 hours');
    END IF;
END $$;

-- Show current policies
SELECT 
    'Current Policies' as info,
    hypertable_name,
    proc_name,
    schedule_interval,
    config
FROM timescaledb_information.jobs 
WHERE proc_name IN ('policy_retention', 'policy_compression');
