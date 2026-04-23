package com.cryptoterm.backend.monitoring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class OtherMetricId implements java.io.Serializable {

    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @Column(name = "metric_type_id", nullable = false)
    private Integer metricTypeId;

    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "sensor_key", nullable = false)
    private String sensorKey;

    protected OtherMetricId() {}

    public OtherMetricId(OffsetDateTime time, Integer metricTypeId, UUID deviceId, String sensorKey) {
        this.time = time;
        this.metricTypeId = metricTypeId;
        this.deviceId = deviceId;
        this.sensorKey = sensorKey;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public Integer getMetricTypeId() {
        return metricTypeId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public String getSensorKey() {
        return sensorKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OtherMetricId that)) return false;
        return Objects.equals(time, that.time)
                && Objects.equals(metricTypeId, that.metricTypeId)
                && Objects.equals(deviceId, that.deviceId)
                && Objects.equals(sensorKey, that.sensorKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, metricTypeId, deviceId, sensorKey);
    }
}
