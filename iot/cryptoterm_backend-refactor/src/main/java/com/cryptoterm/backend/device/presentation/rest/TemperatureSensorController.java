package com.cryptoterm.backend.device.presentation.rest;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.auth.application.port.out.UserRepository;
import com.cryptoterm.backend.device.domain.TemperatureSensor;
import com.cryptoterm.backend.device.application.service.TemperatureSensorService;
import com.cryptoterm.backend.device.presentation.dto.TemperatureSensorDto;
import com.cryptoterm.backend.device.presentation.dto.CreateTemperatureSensorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/temperature-sensors")
@Tag(name = "Температурные датчики", description = "API для управления температурными датчиками")
@SecurityRequirement(name = "bearerAuth")
public class TemperatureSensorController {

    private final TemperatureSensorService temperatureSensorService;
    private final UserRepository userRepository;

    public TemperatureSensorController(
            TemperatureSensorService temperatureSensorService,
            UserRepository userRepository) {
        this.temperatureSensorService = temperatureSensorService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{sensor_id}")
    @Operation(summary = "Получить температурный датчик", 
               description = "Возвращает информацию о температурном датчике по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Датчик найден"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Датчик не найден")
    })
    public ResponseEntity<?> getTemperatureSensor(
            @Parameter(description = "ID датчика") @PathVariable("sensor_id") UUID sensorId,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            TemperatureSensor sensor = temperatureSensorService.getTemperatureSensor(sensorId, user);
            return ResponseEntity.ok(toDto(sensor));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Получить все датчики пользователя", 
               description = "Возвращает список всех температурных датчиков текущего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список датчиков получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<?> getAllTemperatureSensors(Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            List<TemperatureSensor> sensors = temperatureSensorService.getAllTemperatureSensorsByUser(user);
            List<TemperatureSensorDto> dtos = sensors.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/device/{device_id}")
    @Operation(summary = "Получить датчики устройства", 
               description = "Возвращает список температурных датчиков для указанного устройства")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список датчиков получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getTemperatureSensorsByDevice(
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            List<TemperatureSensor> sensors = temperatureSensorService.getAllTemperatureSensorsByDevice(deviceId, user);
            List<TemperatureSensorDto> dtos = sensors.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private TemperatureSensorDto toDto(TemperatureSensor sensor) {
        return new TemperatureSensorDto(
                sensor.getId(),
                sensor.getUser().getId(),
                sensor.getDevice().getId(),
                sensor.getName(),
                sensor.getCreatedAt().toString()
        );
    }
}
