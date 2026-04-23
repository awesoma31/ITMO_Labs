-- =====================================================
-- V15: Improve other_metric system
-- =====================================================
-- 1. Add index for query performance
-- 2. Add retention policy for other_metric
-- 3. Extend metric_type with management fields
-- =====================================================

-- ======================
-- 1. INDEX FOR QUERY PERFORMANCE
-- ======================
-- OtherMetricStrategy queries by (device_id, metric_type_id, time).
-- Without this index every graph query does a full scan.

CREATE INDEX IF NOT EXISTS idx_other_metric_device_type_time
    ON other_metric (device_id, metric_type_id, time DESC);

-- ======================
-- 2. RETENTION FOR other_metric
-- ======================
-- other_metric had no retention — data grew indefinitely.

SELECT set_chunk_time_interval('other_metric', INTERVAL '1 day');
SELECT add_retention_policy('other_metric', INTERVAL '30 days', if_not_exists => TRUE);

-- ======================
-- 3. EXTEND metric_type
-- ======================

ALTER TABLE metric_type
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS display_name TEXT,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT now();
