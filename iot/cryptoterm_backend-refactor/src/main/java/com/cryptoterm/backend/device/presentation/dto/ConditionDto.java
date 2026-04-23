package com.cryptoterm.backend.device.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для представления условия
 */
public record ConditionDto(
    UUID id,
    @JsonProperty("user_id")
    UUID userId,
    @JsonProperty("device_id")
    UUID deviceId,
    String name,
    @JsonProperty("comparison_operator")
    String comparisonOperator,
    @JsonProperty("threshold_value")
    BigDecimal thresholdValue,
    @JsonProperty("created_at")
    String createdAt
) {}
