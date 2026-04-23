package com.cryptoterm.backend.monitoring.domain;

import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.Miner;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "metric")
public class Metric {

    @Id
    @Column(name = "time", nullable = false)
    private OffsetDateTime time;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miner_id")
    private Miner miner;

    @Column(name = "temperature_c")
    private BigDecimal temperatureC;

    @Column(name = "hash_rate_ths")
    private BigDecimal hashRateThs;

    @Column(name = "power_consumption_w")
    private BigDecimal powerConsumptionW;


    public OffsetDateTime getTime() { return time; }
    public void setTime(OffsetDateTime time) { this.time = time; }
    public Device getDevice() { return device; }
    public void setDevice(Device device) { this.device = device; }
    public Miner getMiner() { return miner; }
    public void setMiner(Miner miner) { this.miner = miner; }
    public BigDecimal getTemperatureC() { return temperatureC; }
    public void setTemperatureC(BigDecimal temperatureC) { this.temperatureC = temperatureC; }
    public BigDecimal getHashRateThs() { return hashRateThs; }
    public void setHashRateThs(BigDecimal hashRateThs) { this.hashRateThs = hashRateThs; }
    public BigDecimal getPowerConsumptionW() { return powerConsumptionW; }
    public void setPowerConsumptionW(BigDecimal powerConsumptionW) { this.powerConsumptionW = powerConsumptionW; }
}


