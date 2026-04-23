package com.cryptoterm.backend.web;

import com.cryptoterm.backend.dto.DeviceResponse;
import com.cryptoterm.backend.dto.UserDevicesResponse;
import com.cryptoterm.backend.security.JwtService;
import com.cryptoterm.backend.service.UserDevicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Устройства пользователей", description = "API для управления устройствами пользователей")
@SecurityRequirement(name = "bearerAuth")
public class UserDeviceController {

    private final UserDevicesService userDeviceService;
    private final JwtService jwtService;

    public UserDeviceController(UserDevicesService userDeviceService, 
                                JwtService jwtService) {
        this.userDeviceService = userDeviceService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{user_id}/devices")
    @Operation(summary = "Получить устройства пользователя (legacy)", 
               description = "Возвращает список устройств и их майнеров для указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получен список устройств",
                    content = @Content(schema = @Schema(implementation = UserDevicesResponse.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<UserDevicesResponse> getUserDevicesWithMiners(
            @Parameter(description = "ID пользователя") @PathVariable("user_id") UUID userId) {
        UserDevicesResponse response = userDeviceService.getUserDevicesWithMiners(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{user_id}/devices/detailed")
    @Operation(summary = "Получить детальную информацию об устройствах пользователя", 
               description = "Возвращает полную информацию об устройствах пользователя с проверкой прав доступа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получена информация об устройствах"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getAllUserDevices(
            @Parameter(description = "ID пользователя") @PathVariable("user_id") UUID userId,
            Authentication authentication) {
        try {
            UUID requestingUserId = UUID.fromString(authentication.getName());
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            List<DeviceResponse> devices = userDeviceService.getAllUserDevices(userId, requestingUserId, isAdmin);
            return ResponseEntity.ok(devices);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/devices/{device_id}")
    @Operation(summary = "Получить информацию об устройстве", 
               description = "Возвращает детальную информацию об устройстве с проверкой прав доступа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешно получена информация об устройстве"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Устройство не найдено")
    })
    public ResponseEntity<?> getDeviceById(
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            Authentication authentication) {
        try {
            UUID requestingUserId = UUID.fromString(authentication.getName());
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));

            DeviceResponse device = userDeviceService.getDeviceById(deviceId, requestingUserId, isAdmin);
            return ResponseEntity.ok(device);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}