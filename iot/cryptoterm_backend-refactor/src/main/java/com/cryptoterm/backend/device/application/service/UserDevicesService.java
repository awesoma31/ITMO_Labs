package com.cryptoterm.backend.service;

import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.dto.DeviceMinersDto;
import com.cryptoterm.backend.dto.DeviceResponse;
import com.cryptoterm.backend.dto.MinerDto;
import com.cryptoterm.backend.dto.UserDevicesResponse;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserDevicesService {
    private final DeviceRepository deviceRepository;
    private final MinerRepository minerRepository;

    public UserDevicesService(DeviceRepository deviceRepository,
                              MinerRepository minerRepository) {
        this.deviceRepository = deviceRepository;
        this.minerRepository = minerRepository;
    }

    public UserDevicesResponse getUserDevicesWithMiners(UUID userId) {
        List<Device> userDevices = deviceRepository.findByOwner_Id(userId);

        if (userDevices.isEmpty()) {
            return new UserDevicesResponse(Collections.emptyList());
        }

        List<UUID> deviceIds = userDevices.stream()
                .map(Device::getId)
                .collect(Collectors.toList());

        List<Miner> allMiners = minerRepository.findByDeviceIdIn(deviceIds);

        // Группируем майнеры по устройствам
        Map<UUID, List<MinerDto>> minersByDevice = allMiners.stream()
                .collect(Collectors.groupingBy(
                        miner -> miner.getDevice().getId(),
                        Collectors.mapping(
                                miner -> new MinerDto(
                                        miner.getId(),
                                        miner.getLabel(),
                                        miner.getVendor(),
                                        miner.getModel(),
                                        miner.getMode() != null ? miner.getMode().name() : "STANDARD"
                                ),
                                Collectors.toList()
                        )
                ));

        List<DeviceMinersDto> deviceDtos = userDevices.stream()
                .map(device -> new DeviceMinersDto(
                        device.getId(),
                        device.getName(),
                        null, // description не используется
                        device.getIpAddress(),
                        device.getRegisteredAt(),
                        minersByDevice.getOrDefault(device.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        return new UserDevicesResponse(deviceDtos);
    }

    public List<DeviceResponse> getAllUserDevices(UUID userId, UUID requestingUserId, boolean isAdmin) {
        // Проверка прав доступа
        if (!isAdmin && !userId.equals(requestingUserId)) {
            throw new SecurityException("Access denied to user devices");
        }

        List<Device> userDevices = deviceRepository.findByOwner_Id(userId);

        if (userDevices.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> deviceIds = userDevices.stream()
                .map(Device::getId)
                .collect(Collectors.toList());

        List<Miner> allMiners = minerRepository.findByDeviceIdIn(deviceIds);

        Map<UUID, List<UUID>> minersByDevice = allMiners.stream()
                .collect(Collectors.groupingBy(
                        miner -> miner.getDevice().getId(),
                        Collectors.mapping(Miner::getId, Collectors.toList())
                ));

        return userDevices.stream()
                .map(device -> new DeviceResponse(
                        device.getId(),
                        device.getName(),
                        device.getIpAddress(),
                        device.getRegisteredAt(),
                        minersByDevice.getOrDefault(device.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    public DeviceResponse getDeviceById(UUID deviceId, UUID requestingUserId, boolean isAdmin) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        // Проверка прав доступа
        if (!isAdmin && !device.getOwner().getId().equals(requestingUserId)) {
            throw new SecurityException("Access denied to device");
        }

        List<Miner> miners = minerRepository.findByDeviceIdIn(List.of(deviceId));
        List<UUID> minerIds = miners.stream()
                .map(Miner::getId)
                .collect(Collectors.toList());

        return new DeviceResponse(
                device.getId(),
                device.getName(),
                device.getIpAddress(),
                device.getRegisteredAt(),
                minerIds
        );
    }

    /**
     * Проверяет, принадлежит ли устройство указанному пользователю.
     *
     * @param deviceId ID устройства
     * @param userId   ID пользователя
     * @return true, если устройство принадлежит пользователю, иначе false
     */
    public boolean isDeviceOwnedByUser(UUID deviceId, UUID userId) {
        return deviceRepository.findById(deviceId)
                .map(device -> device.getOwner().getId().equals(userId))
                .orElse(false);
    }
}
