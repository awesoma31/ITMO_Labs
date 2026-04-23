package com.cryptoterm.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MetricDto {
    private final OffsetDateTime time;
    @JsonProperty("miner_id")
    private final UUID minerId;
    @JsonProperty("temperature_c")
    private final BigDecimal temperatureC;
    @JsonProperty("hash_rate_ths")
    private final BigDecimal hashRateThs;

    public MetricDto(OffsetDateTime time, UUID minerId, BigDecimal temperatureC, BigDecimal hashRateThs) {
        this.time = time;
        this.minerId = minerId;
        this.temperatureC = temperatureC;
        this.hashRateThs = hashRateThs;
    }

    public OffsetDateTime getTime() { return time; }
    public UUID getMinerId() { return minerId; }
    public BigDecimal getTemperatureC() { return temperatureC; }
    public BigDecimal getHashRateThs() { return hashRateThs; }
}