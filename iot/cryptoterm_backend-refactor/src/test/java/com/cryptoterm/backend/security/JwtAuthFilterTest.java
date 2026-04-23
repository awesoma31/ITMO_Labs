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

class JwtAuthFilterTest {

    private static final String SECRET = "test-secret-12345678901234567890";
    private JwtAuthFilter filter;
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthFilter(SECRET, new DeletedUserStore());
        jwtService = new JwtService(SECRET, 3600L, 86400L);
    }

    // Verifies that a valid Bearer token results in an authenticated security context
    @Test
    void validToken_authenticates() throws ServletException, IOException {
        String token = jwtService.generateAccessToken(UUID.randomUUID(), "testuser", "user@example.com", "USER");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    // Verifies that an invalid token leaves the context unauthenticated (graceful failure)
    @Test
    void invalidToken_doesNotAuthenticate() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.here");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}


