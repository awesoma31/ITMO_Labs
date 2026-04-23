package com.cryptoterm.backend.web;

import com.cryptoterm.backend.service.MetricsService;
import com.cryptoterm.backend.service.UserDevicesService;
import com.cryptoterm.backend.service.strategy.MetricStrategy;
import com.cryptoterm.backend.service.strategy.TemperatureMetricStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Контроллер для работы с метриками температуры устройств.
 * Использует паттерн Стратегия через TemperatureMetricStrategy.
 */
@RestController
@RequestMapping("/api/metrics/temperature")
@Tag(name = "Метрики температуры", description = "API для получения метрик температуры устройств")
@SecurityRequirement(name = "bearerAuth")
public class TemperatureController {
    
    private final MetricsService metricsService;
    private final UserDevicesService userDevicesService;
    private final TemperatureMetricStrategy temperatureStrategy;
    
    public TemperatureController(MetricsService metricsService, 
                                UserDevicesService userDevicesService,
                                TemperatureMetricStrategy temperatureStrategy) {
        this.metricsService = metricsService;
        this.userDevicesService = userDevicesService;
        this.temperatureStrategy = temperatureStrategy;
    }
    
    @GetMapping("/device/{device_id}")
    @Operation(summary = "Получить агрегированную температуру устройства", 
               description = "Возвращает агрегированные данные по температуре для конкретного устройства за указанный период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены данные о температуре"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - устройство не принадлежит пользователю"),
        @ApiResponse(responseCode = "404", description = "Устройство не найдено")
    })
    public ResponseEntity<?> getDeviceTemperature(
            @Parameter(description = "Идентификатор устройства") @PathVariable("device_id") UUID deviceId,
            @Parameter(description = "Начальная дата (по умолчанию: 24 часа назад)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @Parameter(description = "Конечная дата (по умолчанию: текущее время)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @Parameter(description = "Интервал агрегации (например, '1 minute', '5 minutes', '1 hour')") 
            @RequestParam(defaultValue = "1 minute") String bucket,
            Authentication authentication) {
        try {
            UUID requestingUserId = UUID.fromString(authentication.getName());
            
            // Проверяем роль администратора
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));
            
            // Проверяем, что устройство принадлежит пользователю (или пользователь - администратор)
            if (!isAdmin && !userDevicesService.isDeviceOwnedByUser(deviceId, requestingUserId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Доступ запрещен: устройство не принадлежит пользователю"));
            }
            
            OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
            OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);
            
            List<MetricStrategy.AggregatedMetricPoint> metrics = metricsService.getAggregatedMetricsByDevice(
                    temperatureStrategy, deviceId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(metrics);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{user_id}")
    @Operation(summary = "Получить агрегированную температуру по всем устройствам пользователя", 
               description = "Возвращает агрегированную температуру по всем устройствам указанного пользователя за период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены данные о температуре"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - можно запрашивать только свои данные")
    })
    public ResponseEntity<?> getUserTemperature(
            @Parameter(description = "Идентификатор пользователя") @PathVariable("user_id") UUID userId,
            @Parameter(description = "Начальная дата (по умолчанию: 24 часа назад)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @Parameter(description = "Конечная дата (по умолчанию: текущее время)") 
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @Parameter(description = "Интервал агрегации (например, '1 minute', '5 minutes', '1 hour')") 
            @RequestParam(defaultValue = "1 minute") String bucket,
            Authentication authentication) {
        try {
            UUID requestingUserId = UUID.fromString(authentication.getName());
            
            // Проверяем, что пользователь запрашивает свои данные
            if (!userId.equals(requestingUserId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Доступ запрещен: можно запрашивать только свои данные"));
            }
            
            OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
            OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);
            
            List<MetricStrategy.AggregatedMetricPoint> metrics = metricsService.getAggregatedMetricsByUser(
                    temperatureStrategy, userId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(metrics);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
}

