package com.cryptoterm.backend.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class MetricMessage {
    // UUID Raspberry Pi (device в БД)
    @JsonProperty("deviceId")
    @JsonAlias("id") // поддержка старого формата где id = deviceId
    public UUID deviceId;
    
    // UUID конкретного ASICа или датчика (miner в БД)
    @JsonProperty("instanceId")
    @JsonAlias("minerId") // поддержка старого формата
    public UUID instanceId;
    
    @JsonProperty("temperatureC")
    @JsonAlias("tempC") // поддержка краткого формата
    public BigDecimal temperatureC;
    
    @JsonProperty("hashRateThs")
    @JsonAlias({"hashrate", "hashRateGhs"}) // поддержка альтернативных форматов
    public BigDecimal hashRateThs;
    
    @JsonProperty("powerConsumptionW")
    @JsonAlias("powerConsumption") // поддержка краткого формата
    public BigDecimal powerConsumptionW; // потребление энергии в ваттах
    
    @JsonProperty("timestamp")
    public Long timestamp; // epoch millis
    
    // Флаг для определения, нужна ли конвертация hashrate из GH/s в TH/s
    @JsonProperty("hashrateUnit")
    @JsonAlias("unit")
    public String hashrateUnit; // "GH/s" или "TH/s", по умолчанию TH/s
}


