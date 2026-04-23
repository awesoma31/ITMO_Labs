package com.cryptoterm.backend.web;

import com.cryptoterm.backend.dto.DeviceLogDto;
import com.cryptoterm.backend.service.DeviceLogService;
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

@RestController
@RequestMapping("/api/devices")
@Tag(name = "Логи", description = "API для работы с логами устройств")
@SecurityRequirement(name = "bearerAuth")
public class LogController {

    private final DeviceLogService deviceLogService;
    private final UserDevicesService userDevicesService;

    public LogController(DeviceLogService deviceLogService, UserDevicesService userDevicesService) {
        this.deviceLogService = deviceLogService;
        this.userDevicesService = userDevicesService;
    }

    @GetMapping("/{device_id}/logs")
    @Operation(summary = "Получить логи устройства", 
               description = "Возвращает текстовые логи устройства за указанный период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получены логи"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getDeviceLogs(
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            @Parameter(description = "Начальная дата (по умолчанию: 24 часа назад)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @Parameter(description = "Конечная дата (по умолчанию: текущее время)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            Authentication authentication
    ) {
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
            return ResponseEntity.ok(deviceLogService.getLogsByDevice(deviceId, fromDate, toDate));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Внутренняя ошибка сервера: " + e.getMessage()));
        }
    }
}
