package com.cryptoterm.backend.device.presentation.rest;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.auth.application.port.out.UserRepository;
import com.cryptoterm.backend.device.domain.Condition;
import com.cryptoterm.backend.device.application.service.ConditionService;
import com.cryptoterm.backend.device.presentation.dto.ConditionDto;
import com.cryptoterm.backend.device.presentation.dto.CreateConditionRequest;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conditions")
@Tag(name = "Условия", description = "API для управления условиями")
@SecurityRequirement(name = "bearerAuth")
public class ConditionController {

    private final ConditionService conditionService;
    private final UserRepository userRepository;

    public ConditionController(
            ConditionService conditionService,
            UserRepository userRepository) {
        this.conditionService = conditionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{condition_id}")
    @Operation(summary = "Получить условие", 
               description = "Возвращает информацию об условии по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Условие найдено"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Условие не найдено")
    })
    public ResponseEntity<?> getCondition(
            @Parameter(description = "ID условия") @PathVariable("condition_id") UUID conditionId,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            Condition condition = conditionService.getCondition(conditionId, user);
            return ResponseEntity.ok(toDto(condition));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Получить все условия пользователя", 
               description = "Возвращает список всех условий текущего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список условий получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<?> getAllConditions(Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            List<Condition> conditions = conditionService.getAllConditionsByUser(user);
            List<ConditionDto> dtos = conditions.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/device/{device_id}")
    @Operation(summary = "Получить условия устройства", 
               description = "Возвращает список условий для указанного устройства")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список условий получен"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getConditionsByDevice(
            @Parameter(description = "ID устройства") @PathVariable("device_id") UUID deviceId,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            List<Condition> conditions = conditionService.getAllConditionsByDevice(deviceId, user);
            List<ConditionDto> dtos = conditions.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{condition_id}")
    @Operation(summary = "Обновить условие", 
               description = "Обновляет информацию об условии")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Условие успешно обновлено"),
        @ApiResponse(responseCode = "401", description = "Не авторизован"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Условие не найдено")
    })
    public ResponseEntity<?> updateCondition(
            @Parameter(description = "ID условия") @PathVariable("condition_id") UUID conditionId,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {
        try {
            UUID userId = UUID.fromString(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            String name = (String) updates.get("name");
            Condition.ComparisonOperator operator = updates.containsKey("comparison_operator") 
                    ? Condition.ComparisonOperator.fromSymbol((String) updates.get("comparison_operator"))
                    : null;
            BigDecimal thresholdValue = updates.containsKey("threshold_value")
                    ? new BigDecimal(updates.get("threshold_value").toString())
                    : null;

            Condition condition = conditionService.updateCondition(
                    conditionId, user, name, operator, thresholdValue);
            return ResponseEntity.ok(toDto(condition));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("не найден")) {
                return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Неверный оператор: " + e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private ConditionDto toDto(Condition condition) {
        return new ConditionDto(
                condition.getId(),
                condition.getUser().getId(),
                condition.getDevice().getId(),
                condition.getName(),
                condition.getComparisonOperator().getSymbol(),
                condition.getThresholdValue(),
                condition.getCreatedAt().toString()
        );
    }
}
