package com.cryptoterm.backend.device.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO для создания температурного датчика
 */
public record CreateTemperatureSensorRequest(
    @NotNull(message = "Device ID is required")
    @JsonProperty("device_id")
    UUID deviceId,
    
    @NotBlank(message = "Name is required")
    String name
) {}
