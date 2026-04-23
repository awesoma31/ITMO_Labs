package com.cryptoterm.backend.mqtt;

public final class Topics {
    private Topics() {}

    public static final String METRICS = "rp/metrics";
    public static final String OTHER_METRICS = "rp/other_metrics";
    public static final String LOGS = "rp/logs";
    public static final String COMMAND = "rp/command";
    
    // ASIC HTTP Proxy commands - sent to specific devices
    public static final String DEVICE_COMMANDS = "device/{deviceId}/control";
    
    // ASIC HTTP Proxy command responses - received from devices
    public static final String DEVICE_RESPONSES = "device/{deviceId}/response";
    
    /**
     * Format device-specific command topic
     */
    public static String getDeviceCommandTopic(String deviceId) {
        return DEVICE_COMMANDS.replace("{deviceId}", deviceId);
    }
    
    /**
     * Format device-specific response topic
     */
    public static String getDeviceResponseTopic(String deviceId) {
        return DEVICE_RESPONSES.replace("{deviceId}", deviceId);
    }
}


