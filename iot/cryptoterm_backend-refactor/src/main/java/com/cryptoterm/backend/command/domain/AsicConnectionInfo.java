package com.cryptoterm.backend.command.domain.asic;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Информация о подключении к ASIC.
 * Используется совместно шаблонами и исполняемыми командами.
 * IP адрес определяется RP на основе minerId.
 */
public class AsicConnectionInfo {
    @JsonProperty("firmware")
    private String firmware;
    
    @JsonProperty("port")
    private Integer port;
    
    @JsonProperty("scheme")
    private String scheme;

    public AsicConnectionInfo() {}

    public AsicConnectionInfo(String firmware, Integer port, String scheme) {
        this.firmware = firmware;
        this.port = port;
        this.scheme = scheme;
    }

    public String getFirmware() { return firmware; }
    public void setFirmware(String firmware) { this.firmware = firmware; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }
}
