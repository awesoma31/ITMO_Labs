package com.cryptoterm.backend.device.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * DTO для представления температурного датчика
 */
public record TemperatureSensorDto(
    UUID id,
    @JsonProperty("user_id")
    UUID userId,
    @JsonProperty("device_id")
    UUID deviceId,
    String name,
    @JsonProperty("created_at")
    String createdAt
) {}
