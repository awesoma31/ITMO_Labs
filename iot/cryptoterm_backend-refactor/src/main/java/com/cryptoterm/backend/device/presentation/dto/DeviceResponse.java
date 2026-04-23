package com.cryptoterm.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DeviceResponse(
    UUID id,
    String name,
    @JsonProperty("ip_address")
    String ipAddress,
    @JsonProperty("registered_at")
    OffsetDateTime registeredAt,
    @JsonProperty("miner_ids")
    List<UUID> minerIds
) {}

