package com.cryptoterm.backend.monitoring.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "other_metric")
public class OtherMetric {

    @EmbeddedId
    private OtherMetricId id;

    @Column(name = "miner_id",nullable = true)
    private UUID minerId;

    @Column(name = "value", nullable = false)
    private Double value;

    protected OtherMetric() {}

    public OtherMetric(OtherMetricId id, UUID minerId, Double value) {
        this.id = id;
        this.minerId = minerId;
        this.value = value;
    }

    public OtherMetricId getId() {
        return id;
    }

    public UUID getMinerId() {
        return minerId;
    }

    public Double getValue() {
        return value;
    }

    public OffsetDateTime getTime() {
        return id.getTime();
    }

    public Integer getMetricTypeId() {
        return id.getMetricTypeId();
    }

    public UUID getDeviceId() {
        return id.getDeviceId();
    }

    public String getSensorKey() {
        return id.getSensorKey();
    }
}
