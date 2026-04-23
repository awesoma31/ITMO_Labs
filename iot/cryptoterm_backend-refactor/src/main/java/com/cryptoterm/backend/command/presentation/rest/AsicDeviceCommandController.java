package com.cryptoterm.backend.web;

import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.command.domain.CommandType;
import com.cryptoterm.backend.command.domain.PowerMode;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.dto.AsicCommandResponse;
import com.cryptoterm.backend.mqtt.CommandPublisher;
import com.cryptoterm.backend.device.application.port.out.MinerRepository;
import com.cryptoterm.backend.service.AsicCommandService;
import com.cryptoterm.backend.service.AsicCommandTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Упрощенный REST контроллер для управления командами ASIC устройств.
 * 
 * Предоставляет 6 основных endpoint'ов:
 * 1. Отправить команду на устройство (rpId, asicId, commandId)
 * 2. Получить доступные команды для устройства (по asic_model)
 * 3. Получить детали команды (по commandId)
 * 4. Запланировать команду на определенное время
 * 5. Удалить сохраненную команду (по ID)
 * 6. Сменить режим работы майнера (ECO/STANDARD/OVERCLOCK)
 */
@RestController
@RequestMapping("/api/v1/asic-commands")
@Tag(name = "Команды ASIC устройств", description = "Упрощенный API для управления и выполнения команд ASIC")
@SecurityRequirement(name = "bearerAuth")
public class AsicDeviceCommandController {
    private static final Logger log = LoggerFactory.getLogger(AsicDeviceCommandController.class);
    
    private final MinerRepository minerRepository;
    private final AsicCommandTemplateService templateService;
    private final AsicCommandService commandService;
    private final CommandPublisher commandPublisher;
    
    public AsicDeviceCommandController(
            MinerRepository minerRepository,
            AsicCommandTemplateService templateService,
            AsicCommandService commandService,
            CommandPublisher commandPublisher) {
        this.minerRepository = minerRepository;
        this.templateService = templateService;
        this.commandService = commandService;
        this.commandPublisher = commandPublisher;
    }
    
    /**
     * 1. Отправить команду на устройство
     * POST /api/v1/asic-commands/execute
     * 
     * Проверяет совместимость шаблона команды с моделью майнера.
     */
    @PostMapping("/execute")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Выполнить команду на ASIC устройстве",
        description = """
            Выполнить шаблон команды на конкретном ASIC майнере.
            - rpId: ID устройства Raspberry Pi
            - asicId: ID ASIC майнера (minerId)
            - commandId: Имя/ID шаблона команды
            
            Автоматически проверяет совместимость команды с моделью майнера.
            Имена моделей нормализуются для избежания проблем с регистром.
            IP адрес определяется автоматически RP на основе asicId.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команда успешно отправлена",
                     content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос или несовместимая команда"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваше устройство"),
        @ApiResponse(responseCode = "404", description = "Устройство, майнер или команда не найдены"),
        @ApiResponse(responseCode = "500", description = "Не удалось отправить команду")
    })
    public ResponseEntity<AsicCommandResponse> executeCommand(
            @Parameter(description = "ID устройства Raspberry Pi", required = true)
            @RequestParam("rpId") UUID rpId,
            
            @Parameter(description = "ID ASIC майнера", required = true)
            @RequestParam("asicId") UUID asicId,
            
            @Parameter(description = "Имя/ID шаблона команды", required = true)
            @RequestParam("commandId") String commandId,
            
            Authentication authentication) {
        
        log.info("Пользователь '{}' выполняет команду '{}' на майнере '{}' через устройство '{}'", 
            authentication.getName(), commandId, asicId, rpId);
        
        // Получить майнер и проверить владение
        Miner miner = getMinerAndCheckOwnership(asicId, rpId, authentication);
        
        // Получить шаблон команды
        AsicCommandTemplate template = templateService.getTemplate(commandId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Шаблон команды '" + commandId + "' не найден"
            ));
        
        // Проверить совместимость (нормализовать имена моделей)
        if (!isCommandCompatibleWithMiner(template, miner)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "Команда '%s' (для %s %s) несовместима с майнером %s %s",
                    commandId,
                    template.getMinerVendor(),
                    template.getMinerModel(),
                    miner.getVendor(),
                    miner.getModel()
                )
            );
        }
        
        // Преобразовать шаблон в команду
        AsicHttpProxyCommand command = templateService.templateToCommand(template, miner);
        
        // Сохранить и отправить команду
        return sendCommand(command, commandId, asicId);
    }
    
    /**
     * 2. Получить доступные команды для устройства (по asic_model)
     * GET /api/v1/asic-commands/available
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Получить доступные команды для модели ASIC",
        description = """
            Возвращает список доступных шаблонов команд для конкретной модели ASIC.
            Каждая команда включает:
            - command_id: Имя шаблона (например, "1780_5150", "reboot")
            - command_name: Человекочитаемое описание
            
            Имена моделей нормализуются для сопоставления.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команды успешно получены"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<List<CommandInfoDto>> getAvailableCommands(
            @Parameter(description = "Модель ASIC (например, 'Antminer S19 Pro Hydro')", required = true)
            @RequestParam("asicModel") String asicModel,
            
            @Parameter(description = "Опционально: Производитель ASIC (например, 'Bitmain')")
            @RequestParam(value = "asicVendor", required = false) String asicVendor) {
        
        log.info("Получение доступных команд для модели: '{}', производитель: '{}'", asicModel, asicVendor);
        
        List<AsicCommandTemplate> templates;
        
        if (asicVendor != null && !asicVendor.isBlank()) {
            templates = templateService.getTemplatesForMinerModelAndVendor(
                normalizeModelName(asicModel), 
                normalizeModelName(asicVendor)
            );
        } else {
            templates = templateService.getTemplatesForModel(normalizeModelName(asicModel));
        }
        
        List<CommandInfoDto> commandInfos = templates.stream()
            .map(template -> new CommandInfoDto(
                template.getName(),
                template.getDescription() != null ? template.getDescription() : template.getName()
            ))
            .collect(Collectors.toList());
        
        log.info("Найдено {} команд для модели '{}'", commandInfos.size(), asicModel);
        
        return ResponseEntity.ok(commandInfos);
    }
    
    /**
     * 3. Получить детали команды (по ID)
     * GET /api/v1/asic-commands/{commandId}
     */
    @GetMapping("/{commandId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Получить детали шаблона команды",
        description = "Получить подробную информацию о конкретном шаблоне команды"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команда найдена"),
        @ApiResponse(responseCode = "404", description = "Команда не найдена"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<AsicCommandTemplate> getCommandDetails(
            @Parameter(description = "ID/имя шаблона команды") @PathVariable String commandId) {
        
        return templateService.getTemplate(commandId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 4. Запланировать команду для выполнения
     * POST /api/v1/asic-commands/schedule
     */
    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Запланировать команду для отложенного выполнения",
        description = """
            Запланировать выполнение команды на конкретное время в будущем.
            Требует query параметры: rpId, asicId, commandId, scheduledAt
            
            Пример запроса:
            POST /api/v1/asic-commands/schedule?rpId=<uuid>&asicId=<uuid>&commandId=3495W_132TH&scheduledAt=2026-02-01T10:00:00Z
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Тело запроса не требуется - все параметры передаются через query",
            required = false
        )
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Команда успешно запланирована"),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос или время выполнения не в будущем"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Устройство, майнер или команда не найдены")
    })
    public ResponseEntity<AsicCommandResponse> scheduleCommand(
            @Parameter(description = "ID устройства Raspberry Pi", required = true)
            @RequestParam("rpId") UUID rpId,
            
            @Parameter(description = "ID ASIC майнера", required = true)
            @RequestParam("asicId") UUID asicId,
            
            @Parameter(description = "Имя/ID шаблона команды", required = true)
            @RequestParam("commandId") String commandId,
            
            @Parameter(description = "Время выполнения (ISO-8601)", required = true)
            @RequestParam("scheduledAt") OffsetDateTime scheduledAt,
            
            Authentication authentication) {
        
        log.info("Пользователь '{}' планирует команду '{}' на майнере '{}' на {}", 
            authentication.getName(), commandId, asicId, scheduledAt);
        
        // Проверить, что запланированное время в будущем
        if (!scheduledAt.isAfter(OffsetDateTime.now())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Запланированное время должно быть в будущем"
            );
        }
        
        // Получить майнер и проверить владение
        Miner miner = getMinerAndCheckOwnership(asicId, rpId, authentication);
        
        // Получить шаблон команды
        AsicCommandTemplate template = templateService.getTemplate(commandId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Шаблон команды '" + commandId + "' не найден"
            ));
        
        // Проверить совместимость
        if (!isCommandCompatibleWithMiner(template, miner)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "Команда '%s' несовместима с майнером %s %s",
                    commandId, miner.getVendor(), miner.getModel()
                )
            );
        }
        
        // Преобразовать шаблон в команду
        AsicHttpProxyCommand command = templateService.templateToCommand(template, miner);
        
        // Сохранить команду с запланированным временем
        AsicHttpProxyCommand savedCommand = commandService.createCommand(command, scheduledAt);
        
        log.info("Команда {} запланирована на выполнение в {}", savedCommand.getCmdId(), scheduledAt);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AsicCommandResponse.fromEntity(savedCommand));
    }
    
    /**
     * 5. Удалить команду (по ID)
     * DELETE /api/v1/asic-commands/{commandId}
     * Только для сохраненных/ожидающих команд, не для шаблонов.
     */
    @DeleteMapping("/commands/{cmdId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Удалить сохраненную команду",
        description = "Удалить ранее сохраненную/запланированную команду (не шаблон)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Команда успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Команда не найдена"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<Void> deleteCommand(
            @Parameter(description = "ID команды") @PathVariable String cmdId) {
        
        commandService.deleteCommand(cmdId);
        log.info("Команда {} удалена", cmdId);
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 6. Сменить режим мощности майнера
     * POST /api/v1/asic-commands/change-mode
     */
    @PostMapping("/change-mode")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Изменить режим мощности майнера",
        description = """
            Изменить режим мощности майнера. IP адрес определяется автоматически устройством.
            
            Поддерживаемые режимы (автоматически выбираются из доступных шаблонов, отсортированных по мощности):
            - ECO: Низкое энергопотребление (первый/минимальная мощность шаблон)
            - STANDARD: Сбалансированная мощность и производительность (средний шаблон)
            - OVERCLOCK: Максимальная производительность (предпоследний шаблон)
            
            Команда получит все шаблоны для модели майнера и выберет подходящий на основе режима.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команда смены режима успешно отправлена",
                     content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос или не найден подходящий шаблон"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваше устройство"),
        @ApiResponse(responseCode = "404", description = "Майнер не найден"),
        @ApiResponse(responseCode = "500", description = "Не удалось отправить команду")
    })
    public ResponseEntity<AsicCommandResponse> changeMinerMode(
            @Parameter(description = "ID устройства Raspberry Pi", required = true)
            @RequestParam("rpId") UUID rpId,
            
            @Parameter(description = "ID ASIC майнера", required = true)
            @RequestParam("asicId") UUID asicId,
            
            @Parameter(description = "Режим мощности: ECO, STANDARD, OVERCLOCK", required = true)
            @RequestParam("mode") String mode,
            
            Authentication authentication) {
        
        log.info("Пользователь '{}' меняет режим майнера '{}' на '{}'", 
            authentication.getName(), asicId, mode);
        
        // Распарсить режим
        PowerMode powerMode;
        try {
            powerMode = PowerMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Некорректный режим '" + mode + "'. Допустимые: ECO, STANDARD, OVERCLOCK"
            );
        }
        
        // Получить майнер и проверить владение
        Miner miner = getMinerAndCheckOwnership(asicId, rpId, authentication);
        
        // Получить шаблоны для этой модели
        List<AsicCommandTemplate> templates = templateService.getTemplatesForMinerModelAndVendor(
            normalizeModelName(miner.getModel()),
            normalizeModelName(miner.getVendor())
        );
        
        if (templates.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Шаблоны не найдены для " + miner.getVendor() + " " + miner.getModel()
            );
        }
        
        // Найти только MODE_CHANGE шаблоны
        List<AsicCommandTemplate> modeTemplates = templates.stream()
            .filter(t -> t.getCommandType() == CommandType.MODE_CHANGE)
            .collect(Collectors.toList());
        
        if (modeTemplates.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Шаблоны смены режима не найдены для " + miner.getModel()
            );
        }
        
        // Сортировать по мощности (извлеченной из имени шаблона)
        modeTemplates.sort(Comparator.comparing(t -> {
            Integer power = CommandType.extractPowerWatts(t.getName());
            return power != null ? power : 0;
        }));
        
        // Выбрать шаблон на основе режима
        AsicCommandTemplate selectedTemplate;
        switch (powerMode) {
            case ECO:
                // Первый (минимальная мощность)
                selectedTemplate = modeTemplates.get(0);
                break;
            case STANDARD:
                // Средний
                int middleIndex = modeTemplates.size() / 2;
                selectedTemplate = modeTemplates.get(middleIndex);
                break;
            case OVERCLOCK:
                // Предпоследний (или последний если только один)
                int overclockIndex = Math.max(0, modeTemplates.size() - 2);
                if (modeTemplates.size() == 1) {
                    overclockIndex = 0;
                }
                selectedTemplate = modeTemplates.get(overclockIndex);
                break;
            default:
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Неподдерживаемый режим: " + mode
                );
        }
        
        log.info("Выбран шаблон '{}' для режима {} на майнере {}", 
            selectedTemplate.getName(), mode, asicId);
        
        // Преобразовать шаблон в команду
        AsicHttpProxyCommand command = templateService.templateToCommand(selectedTemplate, miner);
        command.setPowerMode(powerMode.name());
        
        // Сохранить и отправить команду
        return sendCommand(command, "смена режима на " + mode, asicId);
    }
    
    /**
     * 7. Получить историю команд текущего пользователя
     * GET /api/v1/asic-commands/history
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Получить историю команд пользователя",
        description = """
            Возвращает все команды, отправленные на устройства текущего пользователя.
            Отсортировано по дате создания (новые сначала).
            Опционально можно отфильтровать по статусу.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команды успешно получены"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<List<AsicCommandResponse>> getUserCommandHistory(
            @Parameter(description = "Фильтр по статусу: PENDING, SCHEDULED, SENT, EXECUTING, SUCCESS, FAILED, CANCELLED")
            @RequestParam(value = "status", required = false) String status,
            
            Authentication authentication) {
        
        UUID userId = UUID.fromString(authentication.getName());
        log.info("Получение истории команд пользователя {}, фильтр статуса: {}", userId, status);
        
        List<AsicHttpProxyCommand> commands;
        
        if (status != null && !status.isBlank()) {
            try {
                AsicHttpProxyCommand.CommandStatus commandStatus =
                    AsicHttpProxyCommand.CommandStatus.valueOf(status.toUpperCase());
                commands = commandService.getCommandsByUserAndStatus(userId, commandStatus);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некорректный статус '" + status + "'. Допустимые: PENDING, SCHEDULED, SENT, EXECUTING, SUCCESS, FAILED, CANCELLED"
                );
            }
        } else {
            commands = commandService.getCommandsByUser(userId);
        }
        
        List<AsicCommandResponse> responses = commands.stream()
            .map(AsicCommandResponse::fromEntity)
            .collect(Collectors.toList());
        
        log.info("Найдено {} команд для пользователя {}", responses.size(), userId);
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * 8. Получить историю команд для конкретного майнера (ASIC)
     * GET /api/v1/asic-commands/history/miner/{minerId}
     */
    @GetMapping("/history/miner/{minerId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Получить историю команд для конкретного ASIC майнера",
        description = """
            Возвращает все команды, отправленные на конкретный ASIC майнер.
            Проверяет, что текущий пользователь является владельцем майнера (или администратором).
            Отсортировано по дате создания (новые сначала).
            Опционально можно отфильтровать по статусу.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команды успешно получены"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваш майнер"),
        @ApiResponse(responseCode = "404", description = "Майнер не найден")
    })
    public ResponseEntity<List<AsicCommandResponse>> getMinerCommandHistory(
            @Parameter(description = "ID ASIC майнера") @PathVariable UUID minerId,
            
            @Parameter(description = "Фильтр по статусу: PENDING, SCHEDULED, SENT, EXECUTING, SUCCESS, FAILED, CANCELLED")
            @RequestParam(value = "status", required = false) String status,
            
            Authentication authentication) {
        
        log.info("Получение истории команд для майнера {}, фильтр статуса: {}", minerId, status);
        
        // Проверить, что майнер существует и принадлежит пользователю
        Miner miner = minerRepository.findById(minerId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Майнер не найден"
            ));
        
        UUID authenticatedUserId = UUID.fromString(authentication.getName());
        UUID ownerId = miner.getDevice().getOwner().getId();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin && !ownerId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "У вас нет доступа к этому майнеру"
            );
        }
        
        List<AsicHttpProxyCommand> commands;
        
        if (status != null && !status.isBlank()) {
            try {
                AsicHttpProxyCommand.CommandStatus commandStatus =
                    AsicHttpProxyCommand.CommandStatus.valueOf(status.toUpperCase());
                commands = commandService.getCommandsByMinerAndStatus(minerId.toString(), commandStatus);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некорректный статус '" + status + "'. Допустимые: PENDING, SCHEDULED, SENT, EXECUTING, SUCCESS, FAILED, CANCELLED"
                );
            }
        } else {
            commands = commandService.getCommandsByMiner(minerId.toString());
        }
        
        List<AsicCommandResponse> responses = commands.stream()
            .map(AsicCommandResponse::fromEntity)
            .collect(Collectors.toList());
        
        log.info("Найдено {} команд для майнера {}", responses.size(), minerId);
        
        return ResponseEntity.ok(responses);
    }
    
    // ========== Вспомогательные методы ==========
    
    /**
     * Получить майнер и проверить владение и привязку к устройству.
     * Администраторы имеют доступ ко всем майнерам.
     */
    private Miner getMinerAndCheckOwnership(UUID minerId, UUID deviceId, Authentication authentication) {
        Miner miner = minerRepository.findById(minerId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Майнер не найден"
            ));
        
        // Проверить привязку к устройству
        if (!miner.getDevice().getId().equals(deviceId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Майнер не принадлежит указанному устройству"
            );
        }
        
        // Проверить владение - authentication.getName() возвращает userId (subject из JWT)
        UUID authenticatedUserId = UUID.fromString(authentication.getName());
        UUID ownerId = miner.getDevice().getOwner().getId();
        
        // Проверяем, является ли пользователь администратором
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        // Если пользователь не администратор и не владелец - отказываем в доступе
        if (!isAdmin && !ownerId.equals(authenticatedUserId)) {
            log.warn("OWNERSHIP CHECK FAILED - Пользователь {} попытался получить доступ к майнеру {} владельца {}. Device: {}", 
                authenticatedUserId, minerId, ownerId, deviceId);
            log.warn("Miner details - Model: {}, Vendor: {}, Device Name: {}", 
                miner.getModel(), miner.getVendor(), miner.getDevice().getName());
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                String.format("У вас нет доступа к этому майнеру. Ваш ID: %s, ID владельца: %s", 
                    authenticatedUserId, ownerId)
            );
        }
        
        if (isAdmin) {
            log.info("Ownership check passed - User {} is ADMIN, accessing miner {}", authenticatedUserId, minerId);
        } else {
            log.info("Ownership check passed - User {} owns miner {}", authenticatedUserId, minerId);
        }
        
        return miner;
    }
    
    /**
     * Проверить, совместим ли шаблон команды с майнером.
     * Нормализует имена моделей для избежания проблем с регистром и пробелами.
     * Игнорирует проверку vendor если у майнера установлен "default".
     */
    private boolean isCommandCompatibleWithMiner(AsicCommandTemplate template, Miner miner) {
        String templateModel = normalizeModelName(template.getMinerModel());
        String minerModel = normalizeModelName(miner.getModel());
        
        if (!templateModel.equals(minerModel)) {
            log.warn("Модель шаблона '{}' (нормализованная: '{}') не совпадает с моделью майнера '{}' (нормализованная: '{}')", 
                template.getMinerModel(), templateModel, miner.getModel(), minerModel);
            return false;
        }
        
        // Проверить производителя, если указан и майнер не имеет "default" vendor
        if (template.getMinerVendor() != null) {
            String templateVendor = normalizeModelName(template.getMinerVendor());
            String minerVendor = normalizeModelName(miner.getVendor());
            
            // Игнорируем проверку vendor если у майнера "default" или пустой vendor
            boolean isDefaultVendor = minerVendor.isEmpty() || minerVendor.equals("default");
            
            if (!isDefaultVendor && !templateVendor.equals(minerVendor)) {
                log.warn("Производитель шаблона '{}' не совпадает с производителем майнера '{}'", 
                    template.getMinerVendor(), miner.getVendor());
                return false;
            }
            
            if (isDefaultVendor) {
                log.info("Пропускаем проверку производителя для майнера с vendor='{}' (считается совместимым с любым шаблоном)", 
                    miner.getVendor());
            }
        }
        
        return true;
    }
    
    /**
     * Нормализовать имя модели для сравнения.
     * Переводит в нижний регистр и удаляет лишние пробелы.
     */
    private String normalizeModelName(String modelName) {
        if (modelName == null) {
            return "";
        }
        return modelName.trim().toLowerCase().replaceAll("\\s+", " ");
    }
    
    /**
     * Сохранить и отправить команду через MQTT.
     */
    private ResponseEntity<AsicCommandResponse> sendCommand(
            AsicHttpProxyCommand command,
            String commandName,
            UUID minerId) {
        
        // Сохранить команду
        AsicHttpProxyCommand savedCommand = commandService.createCommand(command);
        
        // Отправить команду через MQTT
        boolean sent = commandPublisher.sendAsicProxyCommand(savedCommand);
        
        if (sent) {
            commandService.updateStatus(
                savedCommand.getCmdId(),
                AsicHttpProxyCommand.CommandStatus.SENT
            );
            savedCommand.setStatus(AsicHttpProxyCommand.CommandStatus.SENT);
            
            log.info("Команда {} ({}) успешно отправлена на майнер {}",
                savedCommand.getCmdId(), commandName, minerId);
            
            return ResponseEntity.ok(AsicCommandResponse.fromEntity(savedCommand));
        } else {
            log.error("Не удалось отправить команду {} ({}) на майнер {}",
                savedCommand.getCmdId(), commandName, minerId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AsicCommandResponse.fromEntity(savedCommand));
        }
    }
    
    // ========== DTOs ==========
    
    /**
     * DTO для информации о команде в списке доступных команд.
     */
    public static class CommandInfoDto {
        @Schema(description = "ID/имя шаблона команды", example = "1780_5150")
        private String commandId;
        
        @Schema(description = "Человекочитаемое описание команды", example = "Режим мощности: 1780W / 51.50 TH/s")
        private String commandName;
        
        public CommandInfoDto(String commandId, String commandName) {
            this.commandId = commandId;
            this.commandName = commandName;
        }
        
        public String getCommandId() { return commandId; }
        public void setCommandId(String commandId) { this.commandId = commandId; }
        
        public String getCommandName() { return commandName; }
        public void setCommandName(String commandName) { this.commandName = commandName; }
    }
}
