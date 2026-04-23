package com.cryptoterm.backend.monitoring.domain;

import com.cryptoterm.backend.device.domain.Device;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "device_log")
public class DeviceLog {

    @Id
    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    private String level;

    @Column(columnDefinition = "text")
    private String message;

    @PrePersist
    public void prePersist() {
        if (time == null) {
            time = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }

    public OffsetDateTime getTime() { return time; }
    public void setTime(OffsetDateTime time) { this.time = time; }
    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}


