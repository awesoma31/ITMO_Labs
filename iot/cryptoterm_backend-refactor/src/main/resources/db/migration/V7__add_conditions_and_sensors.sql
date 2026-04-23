-- Create enum for aggregation method
CREATE TYPE aggregation_method AS ENUM ('min', 'max', 'avg');

-- Create enum for comparison operator
CREATE TYPE comparison_operator AS ENUM ('==', '>', '<', '>=', '<=');

-- Temperature sensors table
CREATE TABLE temperature_sensor (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    device_id UUID NOT NULL REFERENCES device(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_temperature_sensor_user_id ON temperature_sensor (user_id);
CREATE INDEX idx_temperature_sensor_device_id ON temperature_sensor (device_id);

-- Conditions table
CREATE TABLE condition (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    device_id UUID NOT NULL REFERENCES device(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    comparison_operator comparison_operator NOT NULL,
    threshold_value NUMERIC(10,2) NOT NULL,
    aggregation_method aggregation_method, -- перемести и создать агрегатор
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_condition_user_id ON condition (user_id);
CREATE INDEX idx_condition_device_id ON condition (device_id);
