package com.cryptoterm.backend.security;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    // This test ensures we can generate an access token without throwing and it looks like a JWT.
    @Test
    void generateAccessToken_basic() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        String token = svc.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");
        assertNotNull(token);
        assertTrue(token.split("\\.").length >= 3, "Should be three-part JWT");
    }

    // This test ensures we can generate a refresh token without throwing and it looks like a JWT.
    @Test
    void generateRefreshToken_basic() {
        JwtService svc = new JwtService("test-secret-12345678901234567890", 3600L, 86400L);
        String token = svc.generateRefreshToken(UUID.randomUUID());
        assertNotNull(token);
        assertTrue(token.split("\\.").length >= 3, "Should be three-part JWT");
    }
}


