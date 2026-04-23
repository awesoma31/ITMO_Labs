package com.cryptoterm.backend.web;

import com.cryptoterm.backend.command.application.factory.AsicCommandFactory;
import com.cryptoterm.backend.command.domain.AsicCommandTemplate;
import com.cryptoterm.backend.command.domain.AsicHttpProxyCommand;
import com.cryptoterm.backend.device.domain.Miner;
import com.cryptoterm.backend.dto.AsicCommandResponse;
import com.cryptoterm.backend.command.presentation.dto.ChangeMinerModeRequest;
import com.cryptoterm.backend.command.presentation.dto.ScheduleChangeMinerModeRequest;
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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для упрощенного управления майнерами.
 * 
 * Этот контроллер предоставляет простые endpoint'ы для типовых операций:
 * - Перезагрузка майнера
 * - Изменение режима работы (ECO, STANDARD, OVERCLOCK)
 * 
 * Использует фабрики команд для автоматического подбора шаблонов.
 */
@RestController
@RequestMapping("/api/v1/miners")
@Tag(name = "Управление майнерами", description = "Упрощенный API управления майнерами")
@SecurityRequirement(name = "bearerAuth")
public class MinerControlController {
    private static final Logger log = LoggerFactory.getLogger(MinerControlController.class);
    
    private final MinerRepository minerRepository;
    private final AsicCommandTemplateService templateService;
    private final AsicCommandService commandService;
    private final CommandPublisher commandPublisher;
    private final List<AsicCommandFactory> commandFactories;
    
    public MinerControlController(
            MinerRepository minerRepository,
            AsicCommandTemplateService templateService,
            AsicCommandService commandService,
            CommandPublisher commandPublisher,
            List<AsicCommandFactory> commandFactories) {
        this.minerRepository = minerRepository;
        this.templateService = templateService;
        this.commandService = commandService;
        this.commandPublisher = commandPublisher;
        this.commandFactories = commandFactories;
    }
    
    @PostMapping("/{minerId}/reboot")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Перезагрузить майнер",
        description = "Отправить команду перезагрузки на майнер. Автоматически выбирает подходящий шаблон на основе модели майнера. IP адрес определяется автоматически устройством."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команда перезагрузки успешно отправлена",
                     content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос или не найден подходящий шаблон"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваш майнер"),
        @ApiResponse(responseCode = "404", description = "Майнер не найден"),
        @ApiResponse(responseCode = "500", description = "Не удалось отправить команду")
    })
    public ResponseEntity<AsicCommandResponse> rebootMiner(
            @Parameter(description = "ID майнера") @PathVariable UUID minerId,
            Authentication authentication) {
        
        log.info("Пользователь '{}' запрашивает перезагрузку майнера '{}'", authentication.getName(), minerId);
        
        // Получить майнер и проверить владельца
        Miner miner = getMinerAndCheckOwnership(minerId, authentication);
        
        // Найти подходящую фабрику
        AsicCommandFactory factory = findFactoryForMiner(miner);
        
        // Получить шаблоны для этой модели
        List<AsicCommandTemplate> templates = templateService.getTemplatesForMinerModelAndVendor(
            miner.getModel(),
            miner.getVendor()
        );
        
        if (templates.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No templates found for " + miner.getVendor() + " " + miner.getModel()
            );
        }
        
        // Создать команду перезагрузки
        AsicHttpProxyCommand command = factory.createRebootCommand(
            miner,
            templates
        );
        
        if (command == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Reboot template not found for " + miner.getModel()
            );
        }
        
        // Отправить команду
        return sendCommand(command, "restart", minerId);
    }

    @PostMapping("/{minerId}/pause-mining")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "поставить майнинг на паузу",
            description = "Отправить команду остановки майнинга на майнер. Автоматически выбирает подходящий шаблон на основе модели майнера. IP адрес определяется автоматически устройством."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Команда остановки майнера успешно отправлена",
                    content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос или не найден подходящий шаблон"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваш майнер"),
            @ApiResponse(responseCode = "404", description = "Майнер не найден"),
            @ApiResponse(responseCode = "500", description = "Не удалось отправить команду")
    })
    public ResponseEntity<AsicCommandResponse> pauseMiner(
            @Parameter(description = "ID майнера") @PathVariable UUID minerId,
            Authentication authentication) {

        log.info("=== PAUSE MINING REQUEST === Пользователь '{}' запрашивает остановку майнинга для майнера '{}', Authorities: {}", 
            authentication.getName(), minerId, authentication.getAuthorities());

        try {
            // Получить майнер и проверить владельца
            Miner miner = getMinerAndCheckOwnership(minerId, authentication);

            // Найти подходящую фабрику
            AsicCommandFactory factory = findFactoryForMiner(miner);

            // Получить шаблоны для этой модели
            List<AsicCommandTemplate> templates = templateService.getTemplatesForMinerModelAndVendor(
                    miner.getModel(),
                    miner.getVendor()
            );

            if (templates.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No templates found for " + miner.getVendor() + " " + miner.getModel()
                );
            }

            // Создать команду перезагрузки
            AsicHttpProxyCommand command = factory.createPauseCommand(
                    miner,
                    templates
            );

            if (command == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "pause template not found for " + miner.getModel()
                );
            }

            // Отправить команду
            return sendCommand(command, "pause-mining", minerId);
        } catch (Exception e) {
            log.error("=== PAUSE MINING FAILED === Exception: {}, Message: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{minerId}/continue-mining")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
            summary = "поставить майнинг на паузу",
            description = "Отправить команду остановки майнинга на майнер. Автоматически выбирает подходящий шаблон на основе модели майнера. IP адрес определяется автоматически устройством."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Команда остановки майнера успешно отправлена",
                    content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос или не найден подходящий шаблон"),
            @ApiResponse(responseCode = "401", description = "Не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваш майнер"),
            @ApiResponse(responseCode = "404", description = "Майнер не найден"),
            @ApiResponse(responseCode = "500", description = "Не удалось отправить команду")
    })
    public ResponseEntity<AsicCommandResponse> continueMiner(
            @Parameter(description = "ID майнера") @PathVariable UUID minerId,
            Authentication authentication) {

        log.info("=== CONTINUE MINING REQUEST === Пользователь '{}' запрашивает продолжение майнинга для майнера '{}', Authorities: {}", 
            authentication.getName(), minerId, authentication.getAuthorities());

        try {
            // Получить майнер и проверить владельца
            Miner miner = getMinerAndCheckOwnership(minerId, authentication);

            // Найти подходящую фабрику
            AsicCommandFactory factory = findFactoryForMiner(miner);

            // Получить шаблоны для этой модели
            List<AsicCommandTemplate> templates = templateService.getTemplatesForMinerModelAndVendor(
                    miner.getModel(),
                    miner.getVendor()
            );

            if (templates.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No templates found for " + miner.getVendor() + " " + miner.getModel()
                );
            }

            // Создать команду перезагрузки
            AsicHttpProxyCommand command = factory.createContinueCommand(
                    miner,
                    templates
            );

            if (command == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "pause template not found for " + miner.getModel()
                );
            }

            // Отправить команду
            return sendCommand(command, "continue-mining", minerId);
        } catch (Exception e) {
            log.error("=== CONTINUE MINING FAILED === Exception: {}, Message: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    @PostMapping("/{minerId}/change-mode")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Изменить режим мощности майнера",
        description = """
            Изменить режим мощности майнера. IP адрес определяется автоматически устройством.
            
            Поддерживаемые режимы (автоматически выбираются из доступных шаблонов, отсортированных по мощности):
            - ECO: Низкое энергопотребление (первый/минимальная мощность шаблон)
            - STANDARD: Сбалансированная мощность и производительность (средний шаблон)
            - OVERCLOCK: Максимальная производительность (предпоследний шаблон)
            
            Вы можете опционально указать точные значения powerWatts и hashrate для точного контроля.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Команда смены режима успешно отправлена",
                     content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос или не найден подходящий шаблон"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваш майнер"),
        @ApiResponse(responseCode = "404", description = "Майнер не найден"),
        @ApiResponse(responseCode = "500", description = "Не удалось отправить команду")
    })
    public ResponseEntity<AsicCommandResponse> changeMinerMode(
            @Parameter(description = "ID майнера") @PathVariable UUID minerId,
            @Valid @RequestBody ChangeMinerModeRequest request,
            Authentication authentication) {
        
        log.info("=== CHANGE MODE REQUEST === Пользователь '{}' запрашивает смену режима майнера '{}' на '{}', Authorities: {}", 
            authentication.getName(), minerId, request.getMode(), authentication.getAuthorities());
        
        try {
            // Получить майнер и проверить владельца
            Miner miner = getMinerAndCheckOwnership(minerId, authentication);
            
            // Найти подходящую фабрику
            AsicCommandFactory factory = findFactoryForMiner(miner);
            
            // Получить шаблоны для этой модели
            List<AsicCommandTemplate> templates = templateService.getTemplatesForMinerModelAndVendor(
                normalizeModelName(miner.getModel()),
                normalizeModelName(miner.getVendor())
            );
            
            if (templates.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No templates found for " + miner.getVendor() + " " + miner.getModel()
                );
            }
            
            // Создать команду изменения режима
            AsicHttpProxyCommand command;
            
            if (request.getPowerWatts() != null && request.getHashrate() != null) {
                // Точное указание мощности и хэшрейта
                command = factory.createModeChangeCommand(
                    miner,
                    request.getPowerWatts(),
                    request.getHashrate(),
                    templates
                );
            } else {
                // Использование предустановленного режима
                command = factory.createModeChangeCommand(
                    miner,
                    request.getMode(),
                    templates
                );
            }
            
            if (command == null) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No suitable template found for mode " + request.getMode()
                );
            }
            
            command.setPowerMode(request.getMode().name());
            
            // Отправить команду
            return sendCommand(command, "mode change to " + request.getMode(), minerId);
        } catch (Exception e) {
            log.error("=== CHANGE MODE FAILED === Exception: {}, Message: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }
    
    @PostMapping("/{minerId}/schedule-change-mode")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(
        summary = "Запланировать изменение режима мощности майнера",
        description = """
            Запланировать изменение режима мощности майнера на определенное время в будущем.
            IP адрес определяется автоматически устройством.
            
            Поддерживаемые режимы (автоматически выбираются из доступных шаблонов, отсортированных по мощности):
            - ECO: Низкое энергопотребление (первый/минимальная мощность шаблон)
            - STANDARD: Сбалансированная мощность и производительность (средний шаблон)
            - OVERCLOCK: Максимальная производительность (предпоследний шаблон)
            
            Вы можете опционально указать точные значения powerWatts и hashrate для точного контроля.
            Время выполнения должно быть в будущем.
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Команда смены режима успешно запланирована",
                     content = @Content(schema = @Schema(implementation = AsicCommandResponse.class))),
        @ApiResponse(responseCode = "400", description = "Некорректный запрос, не найден подходящий шаблон, или время не в будущем"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - не ваш майнер"),
        @ApiResponse(responseCode = "404", description = "Майнер не найден")
    })
    public ResponseEntity<AsicCommandResponse> scheduleChangeMinerMode(
            @Parameter(description = "ID майнера") @PathVariable UUID minerId,
            @Valid @RequestBody ScheduleChangeMinerModeRequest request,
            Authentication authentication) {
        
        log.info("=== SCHEDULE CHANGE MODE REQUEST === Пользователь '{}' планирует смену режима майнера '{}' на '{}' в {}, Authorities: {}", 
            authentication.getName(), minerId, request.getMode(), request.getScheduledAt(), authentication.getAuthorities());
        
        try {
            // Проверить, что запланированное время в будущем
            if (!request.getScheduledAt().isAfter(OffsetDateTime.now())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Запланированное время должно быть в будущем"
                );
            }
            
            // Получить майнер и проверить владельца
            Miner miner = getMinerAndCheckOwnership(minerId, authentication);
            
            // Найти подходящую фабрику
            AsicCommandFactory factory = findFactoryForMiner(miner);
            
            // Получить шаблоны для этой модели
            List<AsicCommandTemplate> templates = templateService.getTemplatesForMinerModelAndVendor(
                normalizeModelName(miner.getModel()),
                normalizeModelName(miner.getVendor())
            );
            
            if (templates.isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No templates found for " + miner.getVendor() + " " + miner.getModel()
                );
            }
            
            // Создать команду изменения режима
            AsicHttpProxyCommand command;
            
            if (request.getPowerWatts() != null && request.getHashrate() != null) {
                // Точное указание мощности и хэшрейта
                command = factory.createModeChangeCommand(
                    miner,
                    request.getPowerWatts(),
                    request.getHashrate(),
                    templates
                );
            } else {
                // Использование предустановленного режима
                command = factory.createModeChangeCommand(
                    miner,
                    request.getMode(),
                    templates
                );
            }
            
            if (command == null) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No suitable template found for mode " + request.getMode()
                );
            }
            
            command.setPowerMode(request.getMode().name());
            
            // Сохранить команду с запланированным временем
            AsicHttpProxyCommand savedCommand = commandService.createCommand(command, request.getScheduledAt());
            
            log.info("Команда {} запланирована на выполнение в {}", savedCommand.getCmdId(), request.getScheduledAt());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(AsicCommandResponse.fromEntity(savedCommand));
        } catch (Exception e) {
            log.error("=== SCHEDULE CHANGE MODE FAILED === Exception: {}, Message: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
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
     * Получить майнер и проверить права владельца.
     */
    private Miner getMinerAndCheckOwnership(UUID minerId, Authentication authentication) {
        log.info("=== Checking ownership for miner {} ===", minerId);
        
        Miner miner = minerRepository.findById(minerId)
            .orElseThrow(() -> {
                log.error("Miner {} not found in database", minerId);
                return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Miner not found"
                );
            });
        
        log.info("Miner found: {}, Device: {}", miner.getId(), miner.getDevice().getId());
        
        // authentication.getName() возвращает userId (subject из JWT)
        UUID authenticatedUserId = UUID.fromString(authentication.getName());
        log.info("Authenticated user ID: {}", authenticatedUserId);
        
        UUID ownerId = miner.getDevice().getOwner().getId();
        log.info("Miner owner ID: {}", ownerId);
        
        if (!ownerId.equals(authenticatedUserId)) {
            log.warn("ACCESS DENIED: User {} attempted to access miner {} owned by {}", 
                authenticatedUserId, minerId, ownerId);
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You don't have access to this miner"
            );
        }
        
        log.info("Ownership check PASSED for user {}", authenticatedUserId);
        return miner;
    }
    
    /**
     * Найти подходящую фабрику для майнера.
     */
    private AsicCommandFactory findFactoryForMiner(Miner miner) {
        log.info("Searching factory for miner: vendor='{}', model='{}'", miner.getVendor(), miner.getModel());
        log.info("Available factories: {}", commandFactories.stream()
            .map(f -> f.getSupportedMinerVendor() + " " + f.getSupportedMinerModel())
            .toList());
        
        AsicCommandFactory factory = commandFactories.stream()
            .filter(f -> {
                boolean supported = f.supports(miner);
                log.info("Factory {} {} {}: {}", 
                    f.getSupportedMinerVendor(), 
                    f.getSupportedMinerModel(), 
                    supported ? "SUPPORTS" : "DOES NOT SUPPORT",
                    miner.getVendor() + " " + miner.getModel());
                return supported;
            })
            .findFirst()
            .orElseThrow(() -> {
                log.error("No factory found for miner: vendor='{}', model='{}'", miner.getVendor(), miner.getModel());
                return new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Miner model " + miner.getVendor() + " " + miner.getModel() + " is not supported"
                );
            });
        
        log.info("Selected factory: {} {}", factory.getSupportedMinerVendor(), factory.getSupportedMinerModel());
        return factory;
    }
    
    /**
     * Сохранить и отправить команду.
     */
    private ResponseEntity<AsicCommandResponse> sendCommand(
            AsicHttpProxyCommand command,
            String operationName,
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
            
            log.info("Command {} ({}) sent successfully to miner {}",
                savedCommand.getCmdId(), operationName, minerId);
            
            return ResponseEntity.ok(AsicCommandResponse.fromEntity(savedCommand));
        } else {
            log.error("Failed to send command {} ({}) to miner {}",
                savedCommand.getCmdId(), operationName, minerId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AsicCommandResponse.fromEntity(savedCommand));
        }
    }
}
