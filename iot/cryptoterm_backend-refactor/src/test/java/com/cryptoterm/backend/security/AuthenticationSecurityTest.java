package com.cryptoterm.backend.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты безопасности аутентификации
 */
class AuthenticationSecurityTest {

    private static final String SECRET = "test-secret-12345678901234567890";
    private JwtAuthFilter filter;
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthFilter(SECRET, new DeletedUserStore());
        jwtService = new JwtService(SECRET, 3600L, 86400L);
    }

    @Test
    void refreshToken_shouldNotAuthenticate() throws ServletException, IOException {
        String refreshToken = jwtService.generateRefreshToken(UUID.randomUUID());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + refreshToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        // Refresh token НЕ должен создавать аутентификацию
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Refresh token НЕ должен создавать аутентификацию");
    }

    @Test
    void accessToken_shouldAuthenticate() throws ServletException, IOException {
        // Создаем access token
        String accessToken = jwtService.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        // Access token должен создать аутентификацию
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(),
                "Access token должен создавать аутентификацию");
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test
    void requestWithoutToken_shouldNotAuthenticate() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Запрос без токена НЕ должен создавать аутентификацию");
    }

    @Test
    void requestWithInvalidToken_shouldNotAuthenticate() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Запрос с невалидным токеном НЕ должен создавать аутентификацию");
    }

    @Test
    void tokenWithWrongSecret_shouldNotAuthenticate() throws ServletException, IOException {
        // Создаем токен с другим секретом
        JwtService wrongSecretService = new JwtService("wrong-secret-12345678901234567890", 3600L, 86400L);
        String token = wrongSecretService.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Токен с неправильным секретом НЕ должен создавать аутентификацию");
    }

    @Test
    void accessToken_shouldContainCorrectRole() throws ServletException, IOException {
        String accessToken = jwtService.generateAccessToken(UUID.randomUUID(), "adminuser", "user@example.com", "ADMIN");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
                "Аутентификация должна содержать правильную роль");
    }
}
