package com.cryptoterm.backend.command.presentation.dto;

import com.cryptoterm.backend.command.domain.PowerMode;
import jakarta.validation.constraints.NotNull;

/**
 * Запрос на изменение режима работы майнера.
 * IP адрес определяется автоматически по ID майнера.
 */
public class ChangeMinerModeRequest {
    
    @NotNull(message = "Power mode is required")
    private PowerMode mode;
    
    // Опциональные параметры для точного указания мощности и хэшрейта
    private Integer powerWatts;
    private Integer hashrate;
    
    public ChangeMinerModeRequest() {}
    
    public ChangeMinerModeRequest(PowerMode mode) {
        this.mode = mode;
    }
    
    public PowerMode getMode() {
        return mode;
    }
    
    public void setMode(PowerMode mode) {
        this.mode = mode;
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
