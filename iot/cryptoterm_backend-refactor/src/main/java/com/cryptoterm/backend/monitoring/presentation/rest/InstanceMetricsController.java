package com.cryptoterm.backend.web;

import com.cryptoterm.backend.service.InstanceMetricsService;
import com.cryptoterm.backend.service.UserDevicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для работы с метриками instances (ASIC или температурных датчиков).
 * 
 * Instance может быть:
 * - ASIC майнером (таблица miner)
 * - Температурным датчиком (таблица temperature_sensor)
 */
@RestController
@RequestMapping("/api/metrics/instances")
@Tag(name = "Instance Metrics", description = "API для получения метрик конкретных instances (ASIC или датчиков)")
@SecurityRequirement(name = "bearerAuth")
public class InstanceMetricsController {
    
    private final InstanceMetricsService instanceMetricsService;
    private final UserDevicesService userDevicesService;
    
    public InstanceMetricsController(InstanceMetricsService instanceMetricsService,
                                    UserDevicesService userDevicesService) {
        this.instanceMetricsService = instanceMetricsService;
        this.userDevicesService = userDevicesService;
    }
    
    @GetMapping("/device/{deviceId}")
    @Operation(
        summary = "Получить все instances для устройства",
        description = """
            Возвращает список всех instances (ASIC и датчиков) для указанного Raspberry Pi устройства.
            
            **deviceId** - UUID Raspberry Pi устройства
            
            Каждый instance содержит:
            - instanceId - UUID instance (ASIC или датчика)
            - type - тип instance (ASIC или TEMPERATURE_SENSOR)
            - name - имя instance
            - deviceId - UUID родительского устройства
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Список instances успешно получен",
            content = @Content(schema = @Schema(implementation = InstanceMetricsService.InstanceInfo.class))
        ),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - устройство не принадлежит пользователю")
    })
    public ResponseEntity<?> getDeviceInstances(
            @Parameter(description = "UUID Raspberry Pi устройства", example = "58a8c94f-114d-4ed0-a8eb-8569ec2838bc")
            @PathVariable UUID deviceId,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            
            // Проверяем роль администратора
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));
            
            // Проверяем доступ к устройству (или пользователь - администратор)
            if (!isAdmin && !userDevicesService.isDeviceOwnedByUser(deviceId, userId)) {
                return ResponseEntity.status(403)
                    .body(Map.of("error", "Доступ запрещен: устройство не принадлежит пользователю"));
            }
            
            List<InstanceMetricsService.InstanceInfo> instances = 
                instanceMetricsService.getDeviceInstances(deviceId);
            
            return ResponseEntity.ok(instances);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
    
    @GetMapping("/device/{deviceId}/instance/{instanceId}")
    @Operation(
        summary = "Получить информацию об instance",
        description = """
            Возвращает информацию о конкретном instance (ASIC или датчике).
            
            **deviceId** - UUID Raspberry Pi устройства
            **instanceId** - UUID конкретного ASIC или датчика
            
            Автоматически определяет тип instance, ища его в таблицах:
            1. miner (ASIC)
            2. temperature_sensor (датчик)
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Информация об instance успешно получена"
        ),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Instance не найден")
    })
    public ResponseEntity<?> getInstanceInfo(
            @Parameter(description = "UUID Raspberry Pi устройства")
            @PathVariable UUID deviceId,
            @Parameter(description = "UUID instance (ASIC или датчика)", example = "f92a754e-0be9-4da0-956b-44712ee8ffcb")
            @PathVariable UUID instanceId,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            
            // Проверяем роль администратора
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));
            
            // Проверяем доступ к устройству (или пользователь - администратор)
            if (!isAdmin && !userDevicesService.isDeviceOwnedByUser(deviceId, userId)) {
                return ResponseEntity.status(403)
                    .body(Map.of("error", "Доступ запрещен: устройство не принадлежит пользователю"));
            }
            
            InstanceMetricsService.InstanceInfo info = 
                instanceMetricsService.getInstanceInfo(instanceId);
            
            // Проверяем, что instance принадлежит указанному устройству
            if (!info.deviceId().equals(deviceId)) {
                return ResponseEntity.status(403)
                    .body(Map.of("error", "Instance не принадлежит указанному устройству"));
            }
            
            return ResponseEntity.ok(info);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", "Instance не найден"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
    
    @GetMapping("/device/{deviceId}/instance/{instanceId}/metrics")
    @Operation(
        summary = "Получить агрегированные метрики для instance",
        description = """
            Возвращает агрегированные метрики для конкретного instance (ASIC или датчика) за указанный период.
            
            **deviceId** - UUID Raspberry Pi устройства
            **instanceId** - UUID конкретного ASIC или датчика
            
            Метрики включают:
            - avgTemperature - средняя температура (°C)
            - avgHashRate - средний хешрейт (TH/s) - только для ASIC
            - avgPowerConsumption - среднее потребление энергии (W) - только для ASIC
            
            **bucket** - интервал агрегации:
            - '1 minute' - каждую минуту
            - '5 minutes' - каждые 5 минут
            - '1 hour' - каждый час
            - '1 day' - каждый день
            """
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Метрики успешно получены"
        ),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Instance не найден")
    })
    public ResponseEntity<?> getInstanceMetrics(
            @Parameter(description = "UUID Raspberry Pi устройства")
            @PathVariable UUID deviceId,
            @Parameter(description = "UUID instance (ASIC или датчика)")
            @PathVariable UUID instanceId,
            @Parameter(description = "Начальная дата (по умолчанию: 24 часа назад)", example = "2026-01-26T00:00:00Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @Parameter(description = "Конечная дата (по умолчанию: текущее время)", example = "2026-01-27T00:00:00Z")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @Parameter(description = "Интервал агрегации", example = "5 minutes")
            @RequestParam(defaultValue = "5 minutes") String bucket,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            
            // Проверяем роль администратора
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));
            
            // Проверяем доступ к устройству (или пользователь - администратор)
            if (!isAdmin && !userDevicesService.isDeviceOwnedByUser(deviceId, userId)) {
                return ResponseEntity.status(403)
                    .body(Map.of("error", "Доступ запрещен: устройство не принадлежит пользователю"));
            }
            
            OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
            OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);
            
            List<InstanceMetricsService.AggregatedInstanceMetric> metrics = 
                instanceMetricsService.getAggregatedMetrics(deviceId, instanceId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(Map.of(
                "deviceId", deviceId,
                "instanceId", instanceId,
                "from", fromDate,
                "to", toDate,
                "bucket", bucket,
                "metrics", metrics
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
}
