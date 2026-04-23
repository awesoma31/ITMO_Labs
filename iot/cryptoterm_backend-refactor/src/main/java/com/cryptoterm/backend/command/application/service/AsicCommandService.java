package com.cryptoterm.backend.service;

import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.application.port.out.AsicCommandRepository;
import com.cryptoterm.backend.util.HmacSignatureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.device.domain.Device;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для управления командами ASIC HTTP Proxy.
 */
@Service
public class AsicCommandService {
    private static final Logger log = LoggerFactory.getLogger(AsicCommandService.class);
    
    private final AsicCommandRepository commandRepository;
    private final DeviceRepository deviceRepository;
    private final HmacSignatureUtil signatureUtil;
    private final ObjectMapper objectMapper;
    
    @Value("${asic.proxy.secret:}")
    private String proxySecret;
    
    @Value("${asic.proxy.signature.enabled:false}")
    private boolean signatureEnabled;
    
    public AsicCommandService(AsicCommandRepository commandRepository,
                             DeviceRepository deviceRepository,
                             HmacSignatureUtil signatureUtil,
                             ObjectMapper objectMapper) {
        this.commandRepository = commandRepository;
        this.deviceRepository = deviceRepository;
        this.signatureUtil = signatureUtil;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Создать новую команду ASIC proxy (немедленное выполнение).
     * Автоматически генерирует cmdId и подпись, если включено.
     */
    @Transactional
    public AsicHttpProxyCommand createCommand(AsicHttpProxyCommand command) {
        return createCommand(command, null);
    }

    /**
     * Создать новую команду ASIC proxy, опционально запланированную на более позднее выполнение.
     * Когда scheduledAt не null, статус устанавливается в SCHEDULED и команда будет отправлена в указанное время.
     */
    @Transactional
    public AsicHttpProxyCommand createCommand(AsicHttpProxyCommand command, OffsetDateTime scheduledAt) {
        // Сгенерировать ID команды, если не предоставлен
        if (command.getCmdId() == null || command.getCmdId().isEmpty()) {
            command.setCmdId(UUID.randomUUID().toString());
        }

        if (scheduledAt != null) {
            command.setScheduledAt(scheduledAt);
            command.setStatus(AsicHttpProxyCommand.CommandStatus.SCHEDULED);
        } else {
            command.setStatus(AsicHttpProxyCommand.CommandStatus.PENDING);
        }
        command.setCreatedAt(OffsetDateTime.now());

        // Сгенерировать подпись, если включено
        if (signatureEnabled && !proxySecret.isEmpty()) {
            String signature = generateSignature(command);
            command.setSignature(signature);
        }

        // Сохранить в MongoDB
        AsicHttpProxyCommand savedCommand = commandRepository.save(command);
        log.info("Создана команда {} для устройства {} (запланирована: {})",
                savedCommand.getCmdId(), savedCommand.getDeviceId(), scheduledAt != null);

        return savedCommand;
    }

    /**
     * Получить запланированные команды, которые готовы к выполнению (scheduledAt <= now).
     */
    public List<AsicHttpProxyCommand> getDueScheduledCommands() {
        return commandRepository.findByStatusAndScheduledAtLessThanEqual(
                AsicHttpProxyCommand.CommandStatus.SCHEDULED, OffsetDateTime.now());
    }
    
    /**
     * Обновить статус команды.
     */
    @Transactional
    public Optional<AsicHttpProxyCommand> updateStatus(String cmdId, 
                                                       AsicHttpProxyCommand.CommandStatus status) {
        Optional<AsicHttpProxyCommand> commandOpt = commandRepository.findById(cmdId);
        
        if (commandOpt.isPresent()) {
            AsicHttpProxyCommand command = commandOpt.get();
            command.setStatus(status);
            command.setUpdatedAt(OffsetDateTime.now());
            
            if (status == AsicHttpProxyCommand.CommandStatus.EXECUTING) {
                command.setExecutedAt(OffsetDateTime.now());
            }
            
            commandRepository.save(command);
            log.info("Обновлен статус команды {} на {}", cmdId, status);
        }
        
        return commandOpt;
    }
    
    /**
     * Обновить команду результатом выполнения.
     */
    @Transactional
    public Optional<AsicHttpProxyCommand> updateResult(String cmdId, 
                                                       AsicHttpProxyCommand.CommandResult result) {
        Optional<AsicHttpProxyCommand> commandOpt = commandRepository.findById(cmdId);
        
        if (commandOpt.isPresent()) {
            AsicHttpProxyCommand command = commandOpt.get();
            command.setResult(result);
            command.setUpdatedAt(OffsetDateTime.now());
            
            // Обновить статус на основе результата
            if ("success".equalsIgnoreCase(result.getStatus())) {
                command.setStatus(AsicHttpProxyCommand.CommandStatus.SUCCESS);
            } else if ("failed".equalsIgnoreCase(result.getStatus())) {
                command.setStatus(AsicHttpProxyCommand.CommandStatus.FAILED);
            }
            
            commandRepository.save(command);
            log.info("Обновлена команда {} с результатом: {}", cmdId, result.getStatus());
        }
        
        return commandOpt;
    }
    
    /**
     * Получить команду по ID.
     */
    public Optional<AsicHttpProxyCommand> getCommand(String cmdId) {
        return commandRepository.findById(cmdId);
    }
    
    /**
     * Получить все команды для устройства.
     */
    public List<AsicHttpProxyCommand> getCommandsByDevice(String deviceId) {
        return commandRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
    }
    
    /**
     * Получить команды по статусу.
     */
    public List<AsicHttpProxyCommand> getCommandsByStatus(AsicHttpProxyCommand.CommandStatus status) {
        return commandRepository.findByStatus(status);
    }
    
    /**
     * Получить команды по устройству и статусу.
     */
    public List<AsicHttpProxyCommand> getCommandsByDeviceAndStatus(String deviceId, 
                                                                    AsicHttpProxyCommand.CommandStatus status) {
        return commandRepository.findByDeviceIdAndStatus(deviceId, status);
    }
    
    /**
     * Получить все команды для конкретного майнера.
     */
    public List<AsicHttpProxyCommand> getCommandsByMiner(String minerId) {
        return commandRepository.findByMinerIdOrderByCreatedAtDesc(minerId);
    }

    /**
     * Получить команды для конкретного майнера, отфильтрованные по статусу.
     */
    public List<AsicHttpProxyCommand> getCommandsByMinerAndStatus(String minerId,
                                                                   AsicHttpProxyCommand.CommandStatus status) {
        return commandRepository.findByMinerIdAndStatusOrderByCreatedAtDesc(minerId, status);
    }

    /**
     * Получить все команды для всех устройств пользователя.
     * Связь: User -> Device (PostgreSQL) -> Command.deviceId (MongoDB).
     */
    public List<AsicHttpProxyCommand> getCommandsByUser(UUID userId) {
        List<Device> devices = deviceRepository.findByOwner_Id(userId);
        if (devices.isEmpty()) {
            return List.of();
        }
        List<String> deviceIds = devices.stream()
                .map(d -> d.getId().toString())
                .collect(Collectors.toList());
        return commandRepository.findByDeviceIdInOrderByCreatedAtDesc(deviceIds);
    }

    /**
     * Получить команды пользователя, отфильтрованные по статусу.
     */
    public List<AsicHttpProxyCommand> getCommandsByUserAndStatus(UUID userId,
                                                                  AsicHttpProxyCommand.CommandStatus status) {
        List<Device> devices = deviceRepository.findByOwner_Id(userId);
        if (devices.isEmpty()) {
            return List.of();
        }
        List<String> deviceIds = devices.stream()
                .map(d -> d.getId().toString())
                .collect(Collectors.toList());
        return commandRepository.findByDeviceIdInAndStatusOrderByCreatedAtDesc(deviceIds, status);
    }

    /**
     * Отменить ожидающую команду.
     */
    @Transactional
    public Optional<AsicHttpProxyCommand> cancelCommand(String cmdId) {
        Optional<AsicHttpProxyCommand> commandOpt = commandRepository.findById(cmdId);
        
        if (commandOpt.isPresent()) {
            AsicHttpProxyCommand command = commandOpt.get();
            
            // Разрешить отмену только ожидающих, запланированных или отправленных команд
            if (command.getStatus() == AsicHttpProxyCommand.CommandStatus.PENDING ||
                command.getStatus() == AsicHttpProxyCommand.CommandStatus.SCHEDULED ||
                command.getStatus() == AsicHttpProxyCommand.CommandStatus.SENT) {
                command.setStatus(AsicHttpProxyCommand.CommandStatus.CANCELLED);
                command.setUpdatedAt(OffsetDateTime.now());
                commandRepository.save(command);
                log.info("Отменена команда {}", cmdId);
            } else {
                log.warn("Невозможно отменить команду {} со статусом {}", cmdId, command.getStatus());
            }
        }
        
        return commandOpt;
    }
    
    /**
     * Удалить команду.
     */
    @Transactional
    public void deleteCommand(String cmdId) {
        commandRepository.deleteById(cmdId);
        log.info("Удалена команда {}", cmdId);
    }
    
    /**
     * Сгенерировать HMAC подпись для команды.
     */
    private String generateSignature(AsicHttpProxyCommand command) {
        try {
            // Преобразовать команду в map (исключая поле signature)
            @SuppressWarnings("unchecked")
            Map<String, Object> commandMap = objectMapper.convertValue(command, Map.class);
            
            // Создать изменяемую копию для удаления полей
            Map<String, Object> mutableMap = new java.util.HashMap<>(commandMap);
            mutableMap.remove("signature");
            mutableMap.remove("status");
            mutableMap.remove("createdAt");
            mutableMap.remove("updatedAt");
            mutableMap.remove("executedAt");
            mutableMap.remove("scheduledAt");
            mutableMap.remove("result");
            
            return signatureUtil.generateSignature(mutableMap, proxySecret);
        } catch (Exception e) {
            log.error("Не удалось сгенерировать подпись для команды {}", command.getCmdId(), e);
            throw new RuntimeException("Не удалось сгенерировать подпись", e);
        }
    }
}
