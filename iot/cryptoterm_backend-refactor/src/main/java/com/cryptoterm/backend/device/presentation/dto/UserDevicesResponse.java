package com.cryptoterm.backend.dto;

import java.util.List;

public class UserDevicesResponse {
    private List<DeviceMinersDto> devices;

    public UserDevicesResponse(List<DeviceMinersDto> devices) {
        this.devices = devices;
    }

    public List<DeviceMinersDto> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceMinersDto> devices) {
        this.devices = devices;
    }
}
