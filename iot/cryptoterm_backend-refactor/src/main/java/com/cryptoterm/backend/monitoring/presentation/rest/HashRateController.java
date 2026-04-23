package com.cryptoterm.backend.web;

import com.cryptoterm.backend.service.MetricsService;
import com.cryptoterm.backend.service.UserDevicesService;
import com.cryptoterm.backend.service.strategy.HashRateMetricStrategy;
import com.cryptoterm.backend.service.strategy.MetricStrategy;
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
 * Контроллер для работы с метриками хэшрейта устройств.
 * Использует паттерн Стратегия через HashRateMetricStrategy.
 */
@RestController
@RequestMapping("/api/metrics/hashrate")
@Tag(name = "Метрики хэшрейта", description = "API для получения метрик хэшрейта устройств")
@SecurityRequirement(name = "bearerAuth")
public class HashRateController {
    
    private final MetricsService metricsService;
    private final UserDevicesService userDevicesService;
    private final HashRateMetricStrategy hashRateStrategy;
    
    public HashRateController(MetricsService metricsService, 
                             UserDevicesService userDevicesService,
                             HashRateMetricStrategy hashRateStrategy) {
        this.metricsService = metricsService;
        this.userDevicesService = userDevicesService;
        this.hashRateStrategy = hashRateStrategy;
    }
    
    @GetMapping("/device/{device_id}")
    @Operation(summary = "Получить агрегированный хэшрейт устройства", 
               description = "Возвращает агрегированные данные по хэшрейту для конкретного устройства за указанный период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены данные о хэшрейте"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - устройство не принадлежит пользователю"),
        @ApiResponse(responseCode = "404", description = "Устройство не найдено")
    })
    public ResponseEntity<?> getDeviceHashRate(
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
                    hashRateStrategy, deviceId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(metrics);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{user_id}")
    @Operation(summary = "Получить агрегированный хэшрейт всех устройств пользователя", 
               description = "Возвращает суммарные агрегированные данные по хэшрейту для всех устройств пользователя за указанный период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены данные о хэшрейте"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - нельзя просматривать данные других пользователей"),
    })
    public ResponseEntity<?> getUserHashRate(
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
            
            // Проверяем роль администратора
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));
            
            // Проверяем доступ (администратор может просматривать данные всех пользователей)
            if (!isAdmin && !requestingUserId.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "Доступ запрещен: нельзя просматривать данные других пользователей"));
            }
            
            OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
            OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);
            
            List<MetricStrategy.AggregatedMetricPoint> metrics = metricsService.getAggregatedMetricsByUser(
                    hashRateStrategy, userId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(metrics);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
    
}

