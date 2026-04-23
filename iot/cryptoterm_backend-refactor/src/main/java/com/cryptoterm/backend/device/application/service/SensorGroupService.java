package com.cryptoterm.backend.device.application.service;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.SensorGroup;
import com.cryptoterm.backend.device.application.port.out.SensorGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class SensorGroupService {
    private final SensorGroupRepository sensorGroupRepository;

    public SensorGroupService(SensorGroupRepository sensorGroupRepository) {
        this.sensorGroupRepository = sensorGroupRepository;
    }

    /**
     * Регистрация группы датчика при регистрации устройства (внутренний метод)
     */
    @Transactional
    public SensorGroup registerSensorGroup(User user, Device device, String name, 
                                          SensorGroup.AggregationMethod aggregationMethod) {
        SensorGroup sensorGroup = new SensorGroup();
        sensorGroup.setId(UUID.randomUUID());
        sensorGroup.setUser(user);
        sensorGroup.setDevice(device);
        sensorGroup.setName(name);
        sensorGroup.setAggregationMethod(aggregationMethod);
        sensorGroup.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return sensorGroupRepository.save(sensorGroup);
    }
}
