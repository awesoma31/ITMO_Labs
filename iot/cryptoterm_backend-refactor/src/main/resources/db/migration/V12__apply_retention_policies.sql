-- =====================================================
-- V12: APPLY RETENTION & COMPRESSION POLICIES
-- =====================================================
-- This migration ensures all policies are applied
-- even if V11 continuous aggregates failed to create
-- All operations are idempotent (safe to run multiple times)
-- =====================================================

-- ======================
-- 1. RETENTION POLICIES
-- ======================

-- Add retention policy for metric table (keep 1 day)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_retention' 
        AND hypertable_name = 'metric'
    ) THEN
        PERFORM add_retention_policy('metric', INTERVAL '1 day');
        RAISE NOTICE 'Added retention policy for metric table';
    ELSE
        RAISE NOTICE 'Retention policy for metric already exists';
    END IF;
END $$;

-- Add retention policy for device_log table (keep 3 days)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_retention' 
        AND hypertable_name = 'device_log'
    ) THEN
        PERFORM add_retention_policy('device_log', INTERVAL '3 days');
        RAISE NOTICE 'Added retention policy for device_log table';
    ELSE
        RAISE NOTICE 'Retention policy for device_log already exists';
    END IF;
END $$;

-- ======================
-- 2. COMPRESSION SETTINGS
-- ======================

-- Enable compression for metric table
DO $$
BEGIN
    -- Check if compression is already enabled
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.compression_settings
        WHERE hypertable_name = 'metric'
    ) THEN
        ALTER TABLE metric SET (
            timescaledb.compress,
            timescaledb.compress_segmentby = 'device_id, miner_id'
        );
        RAISE NOTICE 'Enabled compression for metric table';
    ELSE
        RAISE NOTICE 'Compression for metric already enabled';
    END IF;
END $$;

-- Enable compression for device_log table
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.compression_settings
        WHERE hypertable_name = 'device_log'
    ) THEN
        ALTER TABLE device_log SET (
            timescaledb.compress,
            timescaledb.compress_segmentby = 'device_id, level'
        );
        RAISE NOTICE 'Enabled compression for device_log table';
    ELSE
        RAISE NOTICE 'Compression for device_log already enabled';
    END IF;
END $$;

-- ======================
-- 3. COMPRESSION POLICIES
-- ======================

-- Add compression policy for metric (compress after 6 hours)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_compression' 
        AND hypertable_name = 'metric'
    ) THEN
        PERFORM add_compression_policy('metric', INTERVAL '6 hours');
        RAISE NOTICE 'Added compression policy for metric table';
    ELSE
        RAISE NOTICE 'Compression policy for metric already exists';
    END IF;
END $$;

-- Add compression policy for device_log (compress after 6 hours)
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM timescaledb_information.jobs 
        WHERE proc_name = 'policy_compression' 
        AND hypertable_name = 'device_log'
    ) THEN
        PERFORM add_compression_policy('device_log', INTERVAL '6 hours');
        RAISE NOTICE 'Added compression policy for device_log table';
    ELSE
        RAISE NOTICE 'Compression policy for device_log already exists';
    END IF;
END $$;

-- ======================
-- 4. CONTINUOUS AGGREGATE POLICIES (for aggregates created in V11)
-- ======================

-- Add refresh policy for metric_30m (if view exists)
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_views 
        WHERE schemaname = 'public' AND viewname = 'metric_30m'
    ) THEN
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_refresh_continuous_aggregate' 
            AND hypertable_name = 'metric_30m'
        ) THEN
            PERFORM add_continuous_aggregate_policy(
                'metric_30m',
                start_offset => INTERVAL '8 days',
                end_offset   => INTERVAL '30 minutes',
                schedule_interval => INTERVAL '30 minutes'
            );
            RAISE NOTICE 'Added refresh policy for metric_30m';
        END IF;
        
        -- Add retention policy
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_retention' 
            AND hypertable_name = 'metric_30m'
        ) THEN
            PERFORM add_retention_policy('metric_30m', INTERVAL '7 days');
            RAISE NOTICE 'Added retention policy for metric_30m';
        END IF;
    ELSE
        RAISE NOTICE 'Continuous aggregate metric_30m does not exist (V11 may have failed)';
    END IF;
END $$;

-- Add refresh policy for metric_1h (if view exists)
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_views 
        WHERE schemaname = 'public' AND viewname = 'metric_1h'
    ) THEN
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_refresh_continuous_aggregate' 
            AND hypertable_name = 'metric_1h'
        ) THEN
            PERFORM add_continuous_aggregate_policy(
                'metric_1h',
                start_offset => INTERVAL '35 days',
                end_offset   => INTERVAL '1 hour',
                schedule_interval => INTERVAL '1 hour'
            );
            RAISE NOTICE 'Added refresh policy for metric_1h';
        END IF;
        
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_retention' 
            AND hypertable_name = 'metric_1h'
        ) THEN
            PERFORM add_retention_policy('metric_1h', INTERVAL '30 days');
            RAISE NOTICE 'Added retention policy for metric_1h';
        END IF;
    ELSE
        RAISE NOTICE 'Continuous aggregate metric_1h does not exist';
    END IF;
END $$;

-- Add refresh policy for metric_1d (if view exists)
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_views 
        WHERE schemaname = 'public' AND viewname = 'metric_1d'
    ) THEN
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_refresh_continuous_aggregate' 
            AND hypertable_name = 'metric_1d'
        ) THEN
            PERFORM add_continuous_aggregate_policy(
                'metric_1d',
                start_offset => INTERVAL '400 days',
                end_offset   => INTERVAL '1 day',
                schedule_interval => INTERVAL '1 day'
            );
            RAISE NOTICE 'Added refresh policy for metric_1d';
        END IF;
        
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_retention' 
            AND hypertable_name = 'metric_1d'
        ) THEN
            PERFORM add_retention_policy('metric_1d', INTERVAL '365 days');
            RAISE NOTICE 'Added retention policy for metric_1d';
        END IF;
    ELSE
        RAISE NOTICE 'Continuous aggregate metric_1d does not exist';
    END IF;
END $$;

-- Add refresh policy for device_log_1h (if view exists)
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_views 
        WHERE schemaname = 'public' AND viewname = 'device_log_1h'
    ) THEN
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_refresh_continuous_aggregate' 
            AND hypertable_name = 'device_log_1h'
        ) THEN
            PERFORM add_continuous_aggregate_policy(
                'device_log_1h',
                start_offset => INTERVAL '8 days',
                end_offset   => INTERVAL '1 hour',
                schedule_interval => INTERVAL '1 hour'
            );
            RAISE NOTICE 'Added refresh policy for device_log_1h';
        END IF;
        
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_retention' 
            AND hypertable_name = 'device_log_1h'
        ) THEN
            PERFORM add_retention_policy('device_log_1h', INTERVAL '7 days');
            RAISE NOTICE 'Added retention policy for device_log_1h';
        END IF;
    ELSE
        RAISE NOTICE 'Continuous aggregate device_log_1h does not exist';
    END IF;
END $$;

-- Add refresh policy for device_log_1d (if view exists)
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_views 
        WHERE schemaname = 'public' AND viewname = 'device_log_1d'
    ) THEN
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_refresh_continuous_aggregate' 
            AND hypertable_name = 'device_log_1d'
        ) THEN
            PERFORM add_continuous_aggregate_policy(
                'device_log_1d',
                start_offset => INTERVAL '35 days',
                end_offset   => INTERVAL '1 day',
                schedule_interval => INTERVAL '1 day'
            );
            RAISE NOTICE 'Added refresh policy for device_log_1d';
        END IF;
        
        IF NOT EXISTS (
            SELECT 1 FROM timescaledb_information.jobs 
            WHERE proc_name = 'policy_retention' 
            AND hypertable_name = 'device_log_1d'
        ) THEN
            PERFORM add_retention_policy('device_log_1d', INTERVAL '30 days');
            RAISE NOTICE 'Added retention policy for device_log_1d';
        END IF;
    ELSE
        RAISE NOTICE 'Continuous aggregate device_log_1d does not exist';
    END IF;
END $$;

-- ======================
-- 5. SUMMARY
-- ======================

-- Show all applied policies
DO $$
DECLARE
    policy_count INTEGER;
BEGIN
    SELECT count(*) INTO policy_count
    FROM timescaledb_information.jobs 
    WHERE proc_name IN ('policy_retention', 'policy_compression', 'policy_refresh_continuous_aggregate');
    
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'V12 Migration completed successfully!';
    RAISE NOTICE 'Total policies applied: %', policy_count;
    RAISE NOTICE '==============================================';
END $$;

-- Display all policies
SELECT 
    CASE 
        WHEN proc_name = 'policy_retention' THEN 'Retention'
        WHEN proc_name = 'policy_compression' THEN 'Compression'
        WHEN proc_name = 'policy_refresh_continuous_aggregate' THEN 'Refresh'
    END as policy_type,
    hypertable_name,
    schedule_interval,
    config
FROM timescaledb_information.jobs 
WHERE proc_name IN ('policy_retention', 'policy_compression', 'policy_refresh_continuous_aggregate')
ORDER BY hypertable_name, proc_name;
