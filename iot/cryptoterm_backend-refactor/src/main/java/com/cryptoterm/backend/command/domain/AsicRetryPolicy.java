package com.cryptoterm.backend.command.domain.asic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Retry policy for ASIC command execution.
 * Shared between templates and executable commands.
 */
public class AsicRetryPolicy {
    @JsonProperty("maxRetries")
    private Integer maxRetries;
    
    @JsonProperty("retryDelayMs")
    private Integer retryDelayMs;

    public AsicRetryPolicy() {}

    public AsicRetryPolicy(Integer maxRetries, Integer retryDelayMs) {
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
    }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public Integer getRetryDelayMs() { return retryDelayMs; }
    public void setRetryDelayMs(Integer retryDelayMs) { this.retryDelayMs = retryDelayMs; }
}
