-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Users
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    telegram TEXT,
    password_hash TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Devices (RP units)
CREATE TABLE device (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    ip_address VARCHAR(256),
    registered_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Miners (ASICs) attached to a device
CREATE TABLE miner (
    id UUID PRIMARY KEY,
    device_id UUID NOT NULL REFERENCES device(id) ON DELETE CASCADE,
    vendor TEXT NOT NULL,
    model TEXT NOT NULL,
    label TEXT NOT NULL,
    mode TEXT NOT NULL DEFAULT 'STANDARD' -- OVERCLOCK, ECO, STANDARD
);

-- Metrics: temperature and hash rate over time
CREATE TABLE metric (
--     id BIGSERIAL PRIMARY KEY,
    time TIMESTAMPTZ PRIMARY KEY,
    device_id UUID NOT NULL REFERENCES device(id) ON DELETE CASCADE,
    miner_id UUID REFERENCES miner(id) ON DELETE CASCADE,
    temperature_c NUMERIC(5,2),
    hash_rate_ths NUMERIC(10,2)
);
SELECT create_hypertable('metric', 'time', if_not_exists => TRUE);
CREATE INDEX IF NOT EXISTS idx_metric_device_time ON metric (device_id, time DESC);
CREATE INDEX IF NOT EXISTS idx_metric_miner_time ON metric (miner_id, time DESC);

-- Logs: textual operational logs
CREATE TABLE device_log (
--     id BIGSERIAL PRIMARY KEY,
    time TIMESTAMPTZ PRIMARY KEY,
    device_id UUID NOT NULL REFERENCES device(id) ON DELETE CASCADE,
    level TEXT DEFAULT 'INFO',
    message TEXT NOT NULL
);
SELECT create_hypertable('device_log', 'time', if_not_exists => TRUE);
CREATE INDEX IF NOT EXISTS idx_device_log_device_time ON device_log (device_id, time DESC);

-- Registration events from device (first boot)
CREATE TABLE device_registration (
    id UUID PRIMARY KEY,
    time TIMESTAMPTZ NOT NULL DEFAULT now(),
    device_id UUID REFERENCES device(id) ON DELETE SET NULL,
    email TEXT,
    telegram TEXT,
    ip_address INET
);


