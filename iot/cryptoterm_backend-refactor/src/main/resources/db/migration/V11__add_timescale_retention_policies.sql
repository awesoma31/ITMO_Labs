-- =====================================================
-- TIMESCALE RETENTION & COMPRESSION POLICIES
-- =====================================================
-- Purpose: Exponential data retention strategy
-- - Raw data: 1 day
-- - 30min aggregates: 7 days
-- - 12hour aggregates: 30 days  
-- - Weekly aggregates: 1 year
-- =====================================================

-- ======================
-- METRICS: Multi-resolution storage
-- ======================

-- 1. RAW METRIC DATA (keep 1 day)
SELECT add_retention_policy(
    'metric',
    INTERVAL '1 day',
    if_not_exists => TRUE
);

-- 2. Compress old metric data (after 6 hours)
ALTER TABLE metric SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'device_id, miner_id'
);

SELECT add_compression_policy(
    'metric',
    INTERVAL '6 hours',
    if_not_exists => TRUE
);

-- 3. 30-MINUTE AGGREGATES (keep 7 days)
CREATE MATERIALIZED VIEW IF NOT EXISTS metric_30m
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('30 minutes', time) AS bucket,
    device_id,
    miner_id,
    avg(temperature_c) AS temperature_avg,
    max(temperature_c) AS temperature_max,
    min(temperature_c) AS temperature_min,
    avg(hash_rate_ths) AS hash_rate_avg,
    max(hash_rate_ths) AS hash_rate_max,
    min(hash_rate_ths) AS hash_rate_min,
    count(*) AS sample_count
FROM metric
GROUP BY bucket, device_id, miner_id
WITH NO DATA;

SELECT add_continuous_aggregate_policy(
    'metric_30m',
    start_offset => INTERVAL '8 days',
    end_offset   => INTERVAL '30 minutes',
    schedule_interval => INTERVAL '30 minutes',
    if_not_exists => TRUE
);

SELECT add_retention_policy(
    'metric_30m',
    INTERVAL '7 days',
    if_not_exists => TRUE
);

-- 4. HOURLY AGGREGATES (keep 30 days) - created directly from metric table
CREATE MATERIALIZED VIEW IF NOT EXISTS metric_1h
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', time) AS bucket,
    device_id,
    miner_id,
    avg(temperature_c) AS temperature_avg,
    max(temperature_c) AS temperature_max,
    min(temperature_c) AS temperature_min,
    avg(hash_rate_ths) AS hash_rate_avg,
    max(hash_rate_ths) AS hash_rate_max,
    min(hash_rate_ths) AS hash_rate_min,
    count(*) AS sample_count
FROM metric
GROUP BY bucket, device_id, miner_id
WITH NO DATA;

SELECT add_continuous_aggregate_policy(
    'metric_1h',
    start_offset => INTERVAL '35 days',
    end_offset   => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour',
    if_not_exists => TRUE
);

SELECT add_retention_policy(
    'metric_1h',
    INTERVAL '30 days',
    if_not_exists => TRUE
);

-- 5. DAILY AGGREGATES (keep 1 year) - created directly from metric table
CREATE MATERIALIZED VIEW IF NOT EXISTS metric_1d
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', time) AS bucket,
    device_id,
    miner_id,
    avg(temperature_c) AS temperature_avg,
    max(temperature_c) AS temperature_max,
    min(temperature_c) AS temperature_min,
    avg(hash_rate_ths) AS hash_rate_avg,
    max(hash_rate_ths) AS hash_rate_max,
    min(hash_rate_ths) AS hash_rate_min,
    count(*) AS sample_count
FROM metric
GROUP BY bucket, device_id, miner_id
WITH NO DATA;

SELECT add_continuous_aggregate_policy(
    'metric_1d',
    start_offset => INTERVAL '400 days',
    end_offset   => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 day',
    if_not_exists => TRUE
);

SELECT add_retention_policy(
    'metric_1d',
    INTERVAL '365 days',
    if_not_exists => TRUE
);

-- ======================
-- DEVICE LOGS: Exponential retention
-- ======================

-- 1. RAW LOGS (keep 3 days) - THIS IS CRITICAL FOR DISK SPACE
SELECT add_retention_policy(
    'device_log',
    INTERVAL '3 days',
    if_not_exists => TRUE
);

-- 2. Compress old logs (after 6 hours)
ALTER TABLE device_log SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'device_id, level'
);

SELECT add_compression_policy(
    'device_log',
    INTERVAL '6 hours',
    if_not_exists => TRUE
);

-- 3. HOURLY LOG SUMMARY (keep 7 days) - for ERROR/WARN counts
CREATE MATERIALIZED VIEW IF NOT EXISTS device_log_1h
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', time) AS bucket,
    device_id,
    level,
    count(*) AS log_count,
    string_agg(DISTINCT substring(message, 1, 100), ' | ') AS sample_messages
FROM device_log
GROUP BY bucket, device_id, level
WITH NO DATA;

SELECT add_continuous_aggregate_policy(
    'device_log_1h',
    start_offset => INTERVAL '8 days',
    end_offset   => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour',
    if_not_exists => TRUE
);

SELECT add_retention_policy(
    'device_log_1h',
    INTERVAL '7 days',
    if_not_exists => TRUE
);

-- 4. DAILY LOG SUMMARY (keep 30 days) - created directly from device_log table
CREATE MATERIALIZED VIEW IF NOT EXISTS device_log_1d
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 day', time) AS bucket,
    device_id,
    level,
    count(*) AS log_count
FROM device_log
GROUP BY bucket, device_id, level
WITH NO DATA;

SELECT add_continuous_aggregate_policy(
    'device_log_1d',
    start_offset => INTERVAL '35 days',
    end_offset   => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 day',
    if_not_exists => TRUE
);

SELECT add_retention_policy(
    'device_log_1d',
    INTERVAL '30 days',
    if_not_exists => TRUE
);

-- ======================
-- HELPER VIEWS
-- ======================

-- View to query metrics with automatic resolution selection
-- NOTE: Frontend should query specific tables based on time range
-- This view is for reference only, use specific tables for better performance
COMMENT ON TABLE metric IS 'Raw metrics (5min resolution): use for last 24 hours';
COMMENT ON VIEW metric_30m IS '30min aggregates: use for last 7 days';
COMMENT ON VIEW metric_1h IS 'Hourly aggregates: use for last 30 days';
COMMENT ON VIEW metric_1d IS 'Daily aggregates: use for historical data (up to 1 year)';

COMMENT ON TABLE device_log IS 'Raw logs: use for last 3 days';
COMMENT ON VIEW device_log_1h IS 'Hourly log summary: use for last 7 days';
COMMENT ON VIEW device_log_1d IS 'Daily log summary: use for last 30 days';
