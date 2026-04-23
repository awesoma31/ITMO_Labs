package com.cryptoterm.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AggregatedMetricsResponse(
    List<TemperaturePoint> temperature,
    @JsonProperty("hash_rate")
    List<HashRatePoint> hashRate
) {
    public record TemperaturePoint(
        OffsetDateTime time, 
        @JsonProperty("avg_temperature")
        BigDecimal avgTemperature
    ) {}
    
    public record HashRatePoint(
        OffsetDateTime time, 
        @JsonProperty("avg_hash_rate")
        BigDecimal avgHashRate
    ) {}
}

