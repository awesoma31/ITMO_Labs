-- =====================================================
-- V14: RECREATE CONTINUOUS AGGREGATES WITH power_consumption_w
-- =====================================================
-- V11 aggregates don't include power_consumption_w
-- (added in V6). Recreate them with all columns.
-- Policies are removed automatically when views are dropped.
-- =====================================================

-- ======================
-- 1. DROP OLD AGGREGATES
-- ======================

DROP MATERIALIZED VIEW IF EXISTS metric_30m CASCADE;
DROP MATERIALIZED VIEW IF EXISTS metric_1h CASCADE;
DROP MATERIALIZED VIEW IF EXISTS metric_1d CASCADE;

-- ======================
-- 2. RECREATE WITH power_consumption_w
-- ======================

CREATE MATERIALIZED VIEW metric_30m
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
    avg(power_consumption_w) AS power_consumption_avg,
    count(*) AS sample_count
FROM metric
GROUP BY bucket, device_id, miner_id
WITH NO DATA;

CREATE MATERIALIZED VIEW metric_1h
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
    avg(power_consumption_w) AS power_consumption_avg,
    count(*) AS sample_count
FROM metric
GROUP BY bucket, device_id, miner_id
WITH NO DATA;

CREATE MATERIALIZED VIEW metric_1d
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
    avg(power_consumption_w) AS power_consumption_avg,
    count(*) AS sample_count
FROM metric
GROUP BY bucket, device_id, miner_id
WITH NO DATA;

-- ======================
-- 3. RE-ADD POLICIES
-- ======================

SELECT add_continuous_aggregate_policy('metric_30m',
    start_offset => INTERVAL '8 days',
    end_offset   => INTERVAL '30 minutes',
    schedule_interval => INTERVAL '30 minutes',
    if_not_exists => TRUE
);

SELECT add_continuous_aggregate_policy('metric_1h',
    start_offset => INTERVAL '35 days',
    end_offset   => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour',
    if_not_exists => TRUE
);

SELECT add_continuous_aggregate_policy('metric_1d',
    start_offset => INTERVAL '400 days',
    end_offset   => INTERVAL '1 day',
    schedule_interval => INTERVAL '1 day',
    if_not_exists => TRUE
);

SELECT add_retention_policy('metric_30m', INTERVAL '7 days', if_not_exists => TRUE);
SELECT add_retention_policy('metric_1h', INTERVAL '30 days', if_not_exists => TRUE);
SELECT add_retention_policy('metric_1d', INTERVAL '365 days', if_not_exists => TRUE);

-- ======================
-- 4. NOTE: AGGREGATE REFRESH
-- ======================
-- refresh_continuous_aggregate() cannot run inside a transaction
-- (which Flyway uses). The continuous aggregate policies will
-- auto-populate the views on their next scheduled run:
--   metric_30m  → every 30 minutes
--   metric_1h   → every 1 hour
--   metric_1d   → every 1 day
--
-- To force an immediate refresh after migration, run manually:
--   CALL refresh_continuous_aggregate('metric_30m', NULL, now());
--   CALL refresh_continuous_aggregate('metric_1h', NULL, now());
--   CALL refresh_continuous_aggregate('metric_1d', NULL, now());
