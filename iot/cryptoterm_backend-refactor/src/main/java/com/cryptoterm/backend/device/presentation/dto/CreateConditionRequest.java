package com.cryptoterm.backend.device.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для создания условия
 */
public record CreateConditionRequest(
    @NotNull(message = "Device ID is required")
    @JsonProperty("device_id")
    UUID deviceId,
    
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Comparison operator is required")
    @JsonProperty("comparison_operator")
    String comparisonOperator,
    
    @NotNull(message = "Threshold value is required")
    @JsonProperty("threshold_value")
    BigDecimal thresholdValue
) {}
