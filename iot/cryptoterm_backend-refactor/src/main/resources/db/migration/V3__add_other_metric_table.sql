-- V3__add_other_metric_table.sql

-- Создаем тип метрики, если его нет
CREATE TABLE IF NOT EXISTS metric_type (
    id          SERIAL PRIMARY KEY,
    name        TEXT NOT NULL UNIQUE,
    unit        TEXT NOT NULL,
    description TEXT
);

-- Создаем таблицу метрик, если ее нет
CREATE TABLE IF NOT EXISTS other_metric (
    time            TIMESTAMPTZ NOT NULL,
    metric_type_id  INTEGER NOT NULL REFERENCES metric_type(id),
    device_id       UUID REFERENCES device(id),
    sensor_key      TEXT NOT NULL,
    value           DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (time, metric_type_id, device_id, sensor_key)
    );

-- TimescaleDB сам проверит наличие гипертаблицы внутри функции
SELECT create_hypertable(
               'other_metric',
               'time',
               if_not_exists => TRUE
       );