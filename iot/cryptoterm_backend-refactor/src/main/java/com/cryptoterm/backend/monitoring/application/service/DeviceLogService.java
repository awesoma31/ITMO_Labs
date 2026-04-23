package com.cryptoterm.backend.service;

import com.cryptoterm.backend.monitoring.domain.DeviceLog;
import com.cryptoterm.backend.dto.DeviceLogDto;
import com.cryptoterm.backend.monitoring.application.port.out.DeviceLogRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceLogService {
    DeviceLogRepository deviceLogRepository;
    public DeviceLogService(DeviceLogRepository deviceLogRepository) {
        this.deviceLogRepository = deviceLogRepository;
    }

    public List<DeviceLogDto> getLogsByDevice(UUID deviceId, OffsetDateTime from, OffsetDateTime to) {
        List<DeviceLog> logs = deviceLogRepository.findByDevice_IdAndTimeBetween(deviceId, from, to);
        return logs.stream()
                .map(log -> new DeviceLogDto(log.getTime(), log.getLevel(), log.getMessage()))
                .collect(Collectors.toList());
    }

}
