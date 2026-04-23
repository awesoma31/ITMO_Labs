package com.cryptoterm.backend.device.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "miner")
public class Miner {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String vendor;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MinerMode mode = MinerMode.STANDARD;

    public enum MinerMode { OVERCLOCK, ECO, STANDARD }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public MinerMode getMode() { return mode; }
    public void setMode(MinerMode mode) { this.mode = mode; }
}


