package com.cryptoterm.backend.service;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.mqtt.dto.RegistrationMessage;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.auth.application.port.out.UserRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class DeviceAuthService {
    private final DeviceRepository deviceRepository;
    private final MinerRepository minerRepository;
    private final UserRepository userRepository;

    public DeviceAuthService(DeviceRepository deviceRepository, MinerRepository minerRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.minerRepository = minerRepository;
        this.userRepository = userRepository;
    }

    public Device RegisterDevice(String userEmail, String userTG, String ipAddress) {
        // Pre-register user if not exists
        User user = null;
        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElseGet(() -> {
                User u = new User();
                u.setId(UUID.randomUUID());
                u.setEmail(userEmail);
                u.setTelegram(userTG);
                u.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
                return userRepository.save(u);
            });
        }
        Device device = new Device();
        UUID deviceUUID = UUID.randomUUID();
        device.setId(deviceUUID);
        device.setName("RP_" + userEmail);
        device.setIpAddress(ipAddress);
        device.setRegisteredAt(OffsetDateTime.now(ZoneOffset.UTC));
        if (user != null) {
            device.setOwner(user);
        }
        deviceRepository.save(device);
        return device;
    }

}
