package com.cryptoterm.backend.device.domain;

import com.cryptoterm.backend.auth.domain.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Группа датчиков с методом агрегации
 */
@Entity
@Table(name = "sensor_group")
public class SensorGroup {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregation_method", nullable = false)
    private AggregationMethod aggregationMethod;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public enum AggregationMethod {
        min,  // минимум
        max,  // максимум
        avg   // среднее
    }

    // Constructors
    public SensorGroup() {
    }

    public SensorGroup(UUID id, User user, Device device, String name, AggregationMethod aggregationMethod, OffsetDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.device = device;
        this.name = name;
        this.aggregationMethod = aggregationMethod;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AggregationMethod getAggregationMethod() {
        return aggregationMethod;
    }

    public void setAggregationMethod(AggregationMethod aggregationMethod) {
        this.aggregationMethod = aggregationMethod;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
