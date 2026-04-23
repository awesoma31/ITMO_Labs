-- Create sensor_group table
CREATE TABLE sensor_group (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    device_id UUID NOT NULL REFERENCES device(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    aggregation_method aggregation_method NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sensor_group_user_id ON sensor_group (user_id);
CREATE INDEX idx_sensor_group_device_id ON sensor_group (device_id);
