package com.cryptoterm.backend.device.application.service;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.TemperatureSensor;
import com.cryptoterm.backend.device.application.port.out.TemperatureSensorRepository;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class TemperatureSensorService {
    private final TemperatureSensorRepository temperatureSensorRepository;
    private final DeviceRepository deviceRepository;

    public TemperatureSensorService(
            TemperatureSensorRepository temperatureSensorRepository,
            DeviceRepository deviceRepository) {
        this.temperatureSensorRepository = temperatureSensorRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public TemperatureSensor createTemperatureSensor(User user, UUID deviceId, String name) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        // Check if user owns the device
        if (!device.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("User does not own this device");
        }

        TemperatureSensor sensor = new TemperatureSensor();
        sensor.setId(UUID.randomUUID());
        sensor.setUser(user);
        sensor.setDevice(device);
        sensor.setName(name);
        sensor.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return temperatureSensorRepository.save(sensor);
    }

    @Transactional(readOnly = true)
    public TemperatureSensor getTemperatureSensor(UUID sensorId, User user) {
        TemperatureSensor sensor = temperatureSensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Temperature sensor not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !sensor.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        return sensor;
    }

    @Transactional(readOnly = true)
    public List<TemperatureSensor> getAllTemperatureSensorsByUser(User user) {
        return temperatureSensorRepository.findByUser_Id(user.getId());
    }

    @Transactional(readOnly = true)
    public List<TemperatureSensor> getAllTemperatureSensorsByDevice(UUID deviceId, User user) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !device.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        return temperatureSensorRepository.findByDevice_Id(deviceId);
    }

    @Transactional
    public TemperatureSensor updateTemperatureSensor(UUID sensorId, User user, String name) {
        TemperatureSensor sensor = temperatureSensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Temperature sensor not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !sensor.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        if (name != null) {
            sensor.setName(name);
        }

        return temperatureSensorRepository.save(sensor);
    }

    @Transactional
    public void deleteTemperatureSensor(UUID sensorId, User user) {
        TemperatureSensor sensor = temperatureSensorRepository.findById(sensorId)
                .orElseThrow(() -> new IllegalArgumentException("Temperature sensor not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !sensor.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        temperatureSensorRepository.delete(sensor);
    }

    // Method for device registration (internal use, no auth check)
    @Transactional
    public TemperatureSensor registerTemperatureSensor(User user, Device device, String name) {
        TemperatureSensor sensor = new TemperatureSensor();
        sensor.setId(UUID.randomUUID());
        sensor.setUser(user);
        sensor.setDevice(device);
        sensor.setName(name);
        sensor.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return temperatureSensorRepository.save(sensor);
    }
}
