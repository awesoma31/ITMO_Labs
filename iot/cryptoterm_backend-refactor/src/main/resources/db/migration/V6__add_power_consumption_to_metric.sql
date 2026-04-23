-- Добавляем новую колонку для хранения потребления энергии в ваттах
ALTER TABLE metric 
ADD COLUMN IF NOT EXISTS power_consumption_w NUMERIC(10, 2);

COMMENT ON COLUMN metric.power_consumption_w IS 'Потребление энергии майнером в ваттах';

CREATE INDEX IF NOT EXISTS idx_metric_power_consumption 
ON metric (device_id, time DESC) 
WHERE power_consumption_w IS NOT NULL;
