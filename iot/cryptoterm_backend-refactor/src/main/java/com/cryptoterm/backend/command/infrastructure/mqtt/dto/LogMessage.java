package com.cryptoterm.backend.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class LogMessage {
    @JsonProperty("deviceId")
    public UUID deviceId;
    
    @JsonProperty("level")
    public String level;
    
    @JsonProperty("message")
    public String message;
    
    @JsonProperty("timestamp")
    public Long timestamp; // epoch millis
}


