package com.cryptoterm.backend.web;

import com.cryptoterm.backend.service.UserDevicesService;
import com.cryptoterm.backend.service.strategy.OtherMetricStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/metrics/other")
@Tag(name = "Прочие метрики", description = "API для универсальных численных метрик (температура окружения, влажность и т.д.)")
@SecurityRequirement(name = "bearerAuth")
public class OtherMetricController {

    private final OtherMetricStrategy otherMetricStrategy;
    private final UserDevicesService userDevicesService;

    public OtherMetricController(OtherMetricStrategy otherMetricStrategy,
                                 UserDevicesService userDevicesService) {
        this.otherMetricStrategy = otherMetricStrategy;
        this.userDevicesService = userDevicesService;
    }

    @GetMapping("/{metric_name}/device/{device_id}")
    @Operation(summary = "Агрегированная метрика по имени",
            description = "Возвращает агрегированные данные (AVG) для указанного типа метрики и устройства, сгруппированные по sensor_key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Данные получены"),
        @ApiResponse(responseCode = "403", description = "Устройство не принадлежит пользователю")
    })
    public ResponseEntity<?> getDeviceMetric(
            @Parameter(description = "Имя типа метрики (напр. ambient_temperature)") @PathVariable("metric_name") String metricName,
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(defaultValue = "5 minutes") String bucket,
            Authentication authentication) {
        ResponseEntity<?> accessCheck = checkDeviceAccess(deviceId, authentication);
        if (accessCheck != null) return accessCheck;

        OffsetDateTime fromDate = from != null ? from : OffsetDateTime.now(ZoneOffset.UTC).minusDays(1);
        OffsetDateTime toDate = to != null ? to : OffsetDateTime.now(ZoneOffset.UTC);

        return ResponseEntity.ok(otherMetricStrategy.getAggregatedByDeviceAndType(
                deviceId, metricName, fromDate, toDate, bucket));
    }

    @GetMapping("/device/{device_id}/latest")
    @Operation(summary = "Последние значения всех метрик устройства",
            description = "Возвращает последнее значение каждого типа метрики для каждого sensor_key устройства")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Данные получены"),
        @ApiResponse(responseCode = "403", description = "Устройство не принадлежит пользователю")
    })
    public ResponseEntity<?> getLatest(
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            Authentication authentication) {
        ResponseEntity<?> accessCheck = checkDeviceAccess(deviceId, authentication);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(otherMetricStrategy.getLatestByDevice(deviceId));
    }

    @GetMapping("/device/{device_id}/sensors")
    @Operation(summary = "Список датчиков устройства",
            description = "Возвращает все sensor_key, связанные с устройством, с информацией о типе метрики и времени последнего получения данных")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список датчиков получен"),
        @ApiResponse(responseCode = "403", description = "Устройство не принадлежит пользователю")
    })
    public ResponseEntity<?> getSensors(
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            Authentication authentication) {
        ResponseEntity<?> accessCheck = checkDeviceAccess(deviceId, authentication);
        if (accessCheck != null) return accessCheck;

        return ResponseEntity.ok(otherMetricStrategy.getSensorsByDevice(deviceId));
    }

    private ResponseEntity<?> checkDeviceAccess(UUID deviceId, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMIN"));

        if (!isAdmin && !userDevicesService.isDeviceOwnedByUser(deviceId, userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Access denied: device does not belong to user"));
        }
        return null;
    }
}
