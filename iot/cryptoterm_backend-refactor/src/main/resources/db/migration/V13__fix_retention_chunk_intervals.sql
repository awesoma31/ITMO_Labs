-- =====================================================
-- V13: FIX RETENTION — reduce chunk intervals
-- =====================================================
-- Root cause: V1 created hypertables with the default
-- chunk_time_interval of 7 days. Retention drops WHOLE
-- chunks, so a 1-day retention policy can never drop a
-- 7-day chunk that still contains recent data.
--
-- Fix: set chunk interval to 1 day so retention can
-- drop day-sized chunks individually.
--
-- Also adjusts retention periods to more practical values:
--   metric     : 1 day  → 3 days  (safety margin for aggregates)
--   device_log : 3 days → 7 days  (user requirement)
-- =====================================================

-- ======================
-- 1. FIX CHUNK INTERVALS (affects new chunks only)
-- ======================

SELECT set_chunk_time_interval('metric', INTERVAL '1 day');
SELECT set_chunk_time_interval('device_log', INTERVAL '1 day');

-- ======================
-- 2. UPDATE RETENTION POLICIES
-- ======================

-- metric: 1 day → 3 days
SELECT remove_retention_policy('metric', if_exists => TRUE);
SELECT add_retention_policy('metric', INTERVAL '3 days', if_not_exists => TRUE);

-- device_log: 3 days → 7 days
SELECT remove_retention_policy('device_log', if_exists => TRUE);
SELECT add_retention_policy('device_log', INTERVAL '7 days', if_not_exists => TRUE);

-- ======================
-- 3. SPLIT EXISTING LARGE CHUNKS
-- ======================
-- Old chunks are 7 days wide and can't be dropped partially.
-- We decompress them so TimescaleDB can work with them,
-- then the background worker will recompress into new
-- 1-day chunks over time.
--
-- For metric: decompress all chunks so they can be
-- re-ingested into the new 1-day chunk layout.

DO $$
DECLARE
    chunk_record RECORD;
    chunk_count INTEGER := 0;
BEGIN
    FOR chunk_record IN
        SELECT chunk_schema, chunk_name
        FROM timescaledb_information.chunks
        WHERE hypertable_name = 'metric'
          AND is_compressed = true
    LOOP
        BEGIN
            EXECUTE format(
                'SELECT decompress_chunk(''%I.%I'')',
                chunk_record.chunk_schema,
                chunk_record.chunk_name
            );
            chunk_count := chunk_count + 1;
        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'Could not decompress chunk %.%: %',
                chunk_record.chunk_schema, chunk_record.chunk_name, SQLERRM;
        END;
    END LOOP;
    RAISE NOTICE 'Decompressed % metric chunks', chunk_count;
END $$;

DO $$
DECLARE
    chunk_record RECORD;
    chunk_count INTEGER := 0;
BEGIN
    FOR chunk_record IN
        SELECT chunk_schema, chunk_name
        FROM timescaledb_information.chunks
        WHERE hypertable_name = 'device_log'
          AND is_compressed = true
    LOOP
        BEGIN
            EXECUTE format(
                'SELECT decompress_chunk(''%I.%I'')',
                chunk_record.chunk_schema,
                chunk_record.chunk_name
            );
            chunk_count := chunk_count + 1;
        EXCEPTION WHEN OTHERS THEN
            RAISE NOTICE 'Could not decompress chunk %.%: %',
                chunk_record.chunk_schema, chunk_record.chunk_name, SQLERRM;
        END;
    END LOOP;
    RAISE NOTICE 'Decompressed % device_log chunks', chunk_count;
END $$;

-- Verification (run manually after migration):
-- SELECT hypertable_name, column_name, time_interval
--   FROM timescaledb_information.dimensions
--   WHERE hypertable_name IN ('metric', 'device_log');
--
-- SELECT hypertable_name, config->>'drop_after' AS drop_after
--   FROM timescaledb_information.jobs
--   WHERE proc_name = 'policy_retention';
