package com.cryptoterm.backend.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты безопасности для JWT токенов
 */
class TokenSecurityTest {

    @Test
    void accessToken_shouldHaveTypeAccess() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        String token = svc.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");
        
        Claims claims = svc.validateToken(token);
        String tokenType = claims.get("type", String.class);
        
        assertEquals("access", tokenType, "Access token должен иметь type=access");
    }

    @Test
    void refreshToken_shouldHaveTypeRefresh() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        String token = svc.generateRefreshToken(UUID.randomUUID());
        
        Claims claims = svc.validateToken(token);
        String tokenType = claims.get("type", String.class);
        
        assertEquals("refresh", tokenType, "Refresh token должен иметь type=refresh");
    }

    @Test
    void accessToken_shouldExpireInConfiguredTime() {
        long expirationSeconds = 3600L; // 1 час
        JwtService svc = new JwtService("test-secret-12345678901234567890", expirationSeconds, 86400L);
        String token = svc.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");
        
        Claims claims = svc.validateToken(token);
        long iat = claims.getIssuedAt().getTime() / 1000;
        long exp = claims.getExpiration().getTime() / 1000;
        long actualExpiration = exp - iat;
        
        assertEquals(expirationSeconds, actualExpiration, 
                "Access token должен истекать через " + expirationSeconds + " секунд");
    }

    @Test
    void refreshToken_shouldExpireInConfiguredTime() {
        long refreshExpirationSeconds = 2592000L; // 30 дней
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, refreshExpirationSeconds);
        String token = svc.generateRefreshToken(UUID.randomUUID());
        
        Claims claims = svc.validateToken(token);
        long iat = claims.getIssuedAt().getTime() / 1000;
        long exp = claims.getExpiration().getTime() / 1000;
        long actualExpiration = exp - iat;
        
        assertEquals(refreshExpirationSeconds, actualExpiration, 
                "Refresh token должен истекать через " + refreshExpirationSeconds + " секунд");
    }

    @Test
    void accessToken_shouldContainUserId() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        UUID userId = UUID.randomUUID();
        String token = svc.generateAccessToken(userId, "testuser", "user@example.com", "USER");
        
        Claims claims = svc.validateToken(token);
        String subject = claims.getSubject();
        
        assertEquals(userId.toString(), subject, "Access token должен содержать user ID в subject");
    }

    @Test
    void accessToken_shouldContainRole() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        String token = svc.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");
        
        Claims claims = svc.validateToken(token);
        String role = claims.get("role", String.class);
        
        assertEquals("USER", role, "Access token должен содержать роль");
    }

    @Test
    void refreshToken_shouldNotContainRole() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        String token = svc.generateRefreshToken(UUID.randomUUID());
        
        Claims claims = svc.validateToken(token);
        String role = claims.get("role", String.class);
        
        assertNull(role, "Refresh token НЕ должен содержать роль");
    }

    @Test
    void expiredToken_shouldBeDetected() throws InterruptedException {
        // Создаем токен с очень коротким сроком жизни
        JwtService svc = new JwtService("test-secret-12345678901234567890", 1L, 86400L);
        String token = svc.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");
        
        // Ждем 2 секунды
        Thread.sleep(2000);
        
        assertTrue(svc.isTokenExpired(token), "Токен должен быть определен как истекший");
    }
}
