package com.cryptoterm.backend.monitoring.presentation.rest;

import com.cryptoterm.backend.service.MetricsService;
import com.cryptoterm.backend.service.strategy.MetricStrategy;
import com.cryptoterm.backend.service.strategy.PowerConsumptionMetricStrategy;
import com.cryptoterm.backend.service.UserDevicesService;
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
 * REST контроллер для работы с метриками потребления энергии устройств.
 */
@RestController
@RequestMapping("/api/metrics/power-consumption")
@Tag(name = "Метрики потребления энергии", description = "API для получения метрик потребления энергии")
@SecurityRequirement(name = "bearerAuth")
public class PowerConsumptionController {
    
    private final MetricsService metricsService;
    private final UserDevicesService userDevicesService;
    private final PowerConsumptionMetricStrategy powerConsumptionStrategy;
    
    public PowerConsumptionController(
            MetricsService metricsService, 
            UserDevicesService userDevicesService,
            PowerConsumptionMetricStrategy powerConsumptionStrategy) {
        this.metricsService = metricsService;
        this.userDevicesService = userDevicesService;
        this.powerConsumptionStrategy = powerConsumptionStrategy;
    }
    
    @GetMapping("/device/{device_id}")
    @Operation(
        summary = "Получить потребление энергии устройства", 
        description = "Возвращает агрегированные данные по потреблению энергии для устройства (в ваттах)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Данные получены"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getDevicePowerConsumption(
            @Parameter(description = "Идентификатор устройства") 
            @PathVariable("device_id") UUID deviceId,
            
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
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Доступ запрещен: устройство не принадлежит пользователю"));
            }
            
            OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
            OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);
            
            List<MetricStrategy.AggregatedMetricPoint> metrics = metricsService.getAggregatedMetricsByDevice(
                    powerConsumptionStrategy, deviceId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{user_id}")
    @Operation(
        summary = "Получить потребление энергии всех устройств пользователя", 
        description = "Возвращает агрегированное потребление энергии по всем устройствам (в ваттах)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Данные получены"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getUserPowerConsumption(
            @Parameter(description = "Идентификатор пользователя") 
            @PathVariable("user_id") UUID userId,
            
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
            
            if (!userId.equals(requestingUserId)) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "Доступ запрещен: нельзя просматривать данные других пользователей"));
            }
            
            OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
            OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);
            
            List<MetricStrategy.AggregatedMetricPoint> metrics = metricsService.getAggregatedMetricsByUser(
                    powerConsumptionStrategy, userId, fromDate, toDate, bucket);
            
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
}
