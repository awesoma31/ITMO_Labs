package com.cryptoterm.backend.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class OtherMetricMessage {
    @JsonProperty("deviceId")
    public UUID deviceId;
    
    @JsonProperty("minerId")
    public UUID minerId;        // может быть null
    
    @JsonProperty("metricKey")
    public String metricKey;    // идентификатор сенсора, напр. "A1"
    
    @JsonProperty("metricValue")
    public Double metricValue;  // численное значение
    
    @JsonProperty("metricType")
    public String metricType;
    
    @JsonProperty("timestamp")
    public Long timestamp;      // Epoch millis
}