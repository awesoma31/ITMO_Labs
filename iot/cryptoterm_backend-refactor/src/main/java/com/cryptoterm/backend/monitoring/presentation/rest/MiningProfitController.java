package com.cryptoterm.backend.web;

import com.cryptoterm.backend.service.MiningProfitService;
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
import java.util.Map;
import java.util.UUID;

/**
 * REST контроллер для расчета прибыли от майнинга.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Прибыль от майнинга", description = "API для расчета прибыли от майнинга")
@SecurityRequirement(name = "bearerAuth")
public class MiningProfitController {

    private final MiningProfitService miningProfitService;

    public MiningProfitController(MiningProfitService miningProfitService) {
        this.miningProfitService = miningProfitService;
    }

    @GetMapping("/{user_id}/profit")
    @Operation(summary = "Рассчитать прибыль пользователя", 
               description = "Рассчитывает общую прибыль от майнинга для всех устройств пользователя за период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Прибыль рассчитана"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<?> getUserProfit(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @PathVariable("user_id") UUID userId,
            
            @Parameter(description = "Начальная дата (по умолчанию: 24 часа назад)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            
            @Parameter(description = "Конечная дата (по умолчанию: текущее время)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            
            Authentication authentication) {
        
        UUID requestingUserId = UUID.fromString(authentication.getName());
        
        // Проверяем роль администратора
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        
        // Проверяем доступ (администратор может просматривать данные всех пользователей)
        if (!isAdmin && !requestingUserId.equals(userId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Доступ запрещен: можно запрашивать только свои данные о прибыли"));
        }
        
        return ResponseEntity.ok(miningProfitService.calculate(userId, from, to));
    }
}
