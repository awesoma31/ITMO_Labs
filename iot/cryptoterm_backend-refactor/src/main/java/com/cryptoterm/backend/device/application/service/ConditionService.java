package com.cryptoterm.backend.device.application.service;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.device.domain.Condition;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.device.application.port.out.ConditionRepository;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class ConditionService {
    private final ConditionRepository conditionRepository;
    private final DeviceRepository deviceRepository;

    public ConditionService(
            ConditionRepository conditionRepository,
            DeviceRepository deviceRepository) {
        this.conditionRepository = conditionRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public Condition createCondition(
            User user,
            UUID deviceId,
            String name,
            Condition.ComparisonOperator comparisonOperator,
            BigDecimal thresholdValue) {
        
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        // Check if user owns the device
        if (!device.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("User does not own this device");
        }

        Condition condition = new Condition();
        condition.setId(UUID.randomUUID());
        condition.setUser(user);
        condition.setDevice(device);
        condition.setName(name);
        condition.setComparisonOperator(comparisonOperator);
        condition.setThresholdValue(thresholdValue);
        condition.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return conditionRepository.save(condition);
    }

    @Transactional(readOnly = true)
    public Condition getCondition(UUID conditionId, User user) {
        Condition condition = conditionRepository.findById(conditionId)
                .orElseThrow(() -> new IllegalArgumentException("Condition not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !condition.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        return condition;
    }

    @Transactional(readOnly = true)
    public List<Condition> getAllConditionsByUser(User user) {
        return conditionRepository.findByUser_Id(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Condition> getAllConditionsByDevice(UUID deviceId, User user) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !device.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        return conditionRepository.findByDevice_Id(deviceId);
    }

    @Transactional
    public Condition updateCondition(
            UUID conditionId,
            User user,
            String name,
            Condition.ComparisonOperator comparisonOperator,
            BigDecimal thresholdValue) {
        
        Condition condition = conditionRepository.findById(conditionId)
                .orElseThrow(() -> new IllegalArgumentException("Condition not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !condition.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        if (name != null) {
            condition.setName(name);
        }
        if (comparisonOperator != null) {
            condition.setComparisonOperator(comparisonOperator);
        }
        if (thresholdValue != null) {
            condition.setThresholdValue(thresholdValue);
        }

        return conditionRepository.save(condition);
    }

    @Transactional
    public void deleteCondition(UUID conditionId, User user) {
        Condition condition = conditionRepository.findById(conditionId)
                .orElseThrow(() -> new IllegalArgumentException("Condition not found"));

        // Check access: admin or owner
        if (!user.getRole().equals("ADMIN") && !condition.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        conditionRepository.delete(condition);
    }

    /**
     * Регистрация условия при регистрации устройства (внутренний метод)
     */
    @Transactional
    public Condition registerCondition(
            User user,
            Device device,
            String name,
            Condition.ComparisonOperator comparisonOperator,
            BigDecimal thresholdValue) {
        
        Condition condition = new Condition();
        condition.setId(UUID.randomUUID());
        condition.setUser(user);
        condition.setDevice(device);
        condition.setName(name);
        condition.setComparisonOperator(comparisonOperator);
        condition.setThresholdValue(thresholdValue);
        condition.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return conditionRepository.save(condition);
    }
}
