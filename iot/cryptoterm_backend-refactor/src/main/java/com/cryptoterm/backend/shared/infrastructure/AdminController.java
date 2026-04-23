package com.cryptoterm.backend.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Администратор", description = "API для администраторов")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Value("${device.registration.api-key:default-device-key-change-in-production}")
    private String deviceRegistrationApiKey;

    @GetMapping("/device-api-key")
    @Operation(summary = "Получить API ключ для регистрации устройств", 
               description = "Возвращает DEVICE_REGISTRATION_API_KEY. Доступно только администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API ключ успешно получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен - требуется роль ADMIN")
    })
    public ResponseEntity<?> getDeviceApiKey(Authentication authentication) {
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Доступ запрещен. Требуется роль администратора.");
        }
        
        return ResponseEntity.ok(Map.of(
            "deviceRegistrationApiKey", deviceRegistrationApiKey,
            "usage", "Добавьте заголовок 'X-API-Key: " + deviceRegistrationApiKey + "' к запросу /api/deviceAuth/signup"
        ));
    }
}
