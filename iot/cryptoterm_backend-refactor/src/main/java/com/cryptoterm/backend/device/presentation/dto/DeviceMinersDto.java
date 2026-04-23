package com.cryptoterm.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для представления устройства с его майнерами
 */
public class DeviceMinersDto {
    private UUID id;
    private String name;
    private String description;
    @JsonProperty("ip_address")
    private String ipAddress;
    @JsonProperty("registered_at")
    private OffsetDateTime registeredAt;
    private List<MinerDto> miners;

    public DeviceMinersDto(UUID id, String name, String description, String ipAddress, 
                          OffsetDateTime registeredAt, List<MinerDto> miners) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ipAddress = ipAddress;
        this.registeredAt = registeredAt;
        this.miners = miners;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(OffsetDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public List<MinerDto> getMiners() {
        return miners;
    }

    public void setMiners(List<MinerDto> miners) {
        this.miners = miners;
    }
}
