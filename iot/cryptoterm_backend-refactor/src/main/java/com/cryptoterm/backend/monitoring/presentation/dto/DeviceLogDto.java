package com.cryptoterm.backend.dto;

import java.time.OffsetDateTime;

public class DeviceLogDto {
    private OffsetDateTime time;
    private String level;
    private String message;

    public DeviceLogDto(OffsetDateTime time, String level, String message) {
        this.time = time;
        this.level = level;
        this.message = message;
    }

    public OffsetDateTime getTime() { return time; }
    public String getLevel() { return level; }
    public String getMessage() { return message; }
}