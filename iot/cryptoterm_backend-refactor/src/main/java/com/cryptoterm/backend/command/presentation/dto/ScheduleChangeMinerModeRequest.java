package com.cryptoterm.backend.command.presentation.dto;

import com.cryptoterm.backend.command.domain.PowerMode;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * Запрос на планирование изменения режима работы майнера на определенное время.
 * IP адрес определяется автоматически по ID майнера.
 */
public class ScheduleChangeMinerModeRequest {
    
    @NotNull(message = "Power mode is required")
    private PowerMode mode;
    
    @NotNull(message = "Scheduled time is required")
    private OffsetDateTime scheduledAt;
    
    // Опциональные параметры для точного указания мощности и хэшрейта
    private Integer powerWatts;
    private Integer hashrate;
    
    public ScheduleChangeMinerModeRequest() {}
    
    public ScheduleChangeMinerModeRequest(PowerMode mode, OffsetDateTime scheduledAt) {
        this.mode = mode;
        this.scheduledAt = scheduledAt;
    }
    
    public PowerMode getMode() {
        return mode;
    }
    
    public void setMode(PowerMode mode) {
        this.mode = mode;
    }
    
    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }
    
    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
    
    public Integer getPowerWatts() {
        return powerWatts;
    }
    
    public void setPowerWatts(Integer powerWatts) {
        this.powerWatts = powerWatts;
    }
    
    public Integer getHashrate() {
        return hashrate;
    }
    
    public void setHashrate(Integer hashrate) {
        this.hashrate = hashrate;
    }
}
