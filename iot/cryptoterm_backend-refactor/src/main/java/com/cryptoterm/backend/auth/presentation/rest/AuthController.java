package com.cryptoterm.backend.web;

import com.cryptoterm.backend.dto.AuthResponse;
import com.cryptoterm.backend.security.JwtService;
import com.cryptoterm.backend.service.AuthService;
import com.cryptoterm.backend.validation.StrongPassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для аутентификации и регистрации пользователей")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    public record RegisterRequest(
            @NotBlank(message = "Username is required")
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,

            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            String email,

            @NotBlank(message = "Password is required")
            @StrongPassword
            String password,

            String telegram
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Username or email is required")
            @com.fasterxml.jackson.annotation.JsonProperty("username_or_email")
            String usernameOrEmail,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token is required")
            @com.fasterxml.jackson.annotation.JsonProperty("refresh_token")
            String refreshToken
    ) {}

    public record DeleteAccountRequest(
            @NotBlank(message = "Password is required for account deletion")
            String password
    ) {}

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя",
            description = "Завершает регистрацию для предзарегистрированного пользователя (создан при регистрации устройства)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные или пользователь уже зарегистрирован",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не предзарегистрирован",
                    content = @Content)
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            AuthResponse response = authService.register(req.username(), req.email(), req.password(), req.telegram());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Check if this is a "user not found" error (pre-registration required)
            if (e.getMessage().contains("User not found") || e.getMessage().contains("pre-registered")) {
                return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
            }
            // Other validation errors
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed"));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему",
            description = "Аутентификация пользователя по username/email и паролю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные учетные данные",
                    content = @Content)
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse response = authService.login(req.usernameOrEmail(), req.password());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login failed"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление access токена",
            description = "Получение нового access и refresh токена используя действующий refresh токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Невалидный или истекший refresh токен",
                    content = @Content)
    })
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest req) {
        try {
            AuthResponse response = authService.refreshToken(req.refreshToken());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Token refresh failed"));
        }
    }

    @DeleteMapping("/account")
    @Operation(summary = "Удаление аккаунта",
            description = "Полное удаление аккаунта пользователя. Требует авторизацию и подтверждение пароля. " +
                    "Удаляет все данные пользователя: устройства, майнеры, метрики, логи, команды. " +
                    "Все токены немедленно инвалидируются.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аккаунт успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Неверный пароль"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DeleteAccountRequest req) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(token);
            authService.deleteAccount(userId, req.password());
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account deletion failed"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы",
            description = "Инвалидирует refresh токен пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            UUID userId = jwtService.extractUserId(token);
            authService.logout(userId);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Logout failed"));
        }
    }
}


