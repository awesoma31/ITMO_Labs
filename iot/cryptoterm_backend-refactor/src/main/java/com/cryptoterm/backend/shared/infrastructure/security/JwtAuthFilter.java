package com.cryptoterm.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final Key key;
    private final DeletedUserStore deletedUserStore;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret, DeletedUserStore deletedUserStore) {
        this.key = deriveHmacKey(secret);
        this.deletedUserStore = deletedUserStore;
    }

    private Key deriveHmacKey(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
                String subject = claims.getSubject();
                String role = claims.get("role", String.class);
                String tokenType = claims.get("type", String.class);
                
                System.out.println("[JwtAuthFilter] " + method + " " + requestUri + " - Token type: " + tokenType + ", Role: " + role);
                
                // Проверка: только access токены допускаются для аутентификации
                if (!"access".equals(tokenType)) {
                    System.out.println("[JwtAuthFilter] Token rejected - not an access token");
                    filterChain.doFilter(request, response);
                    return;
                }
                
                if (subject != null) {
                    // Reject tokens for deleted users
                    try {
                        UUID userId = UUID.fromString(subject);
                        if (deletedUserStore.isDeleted(userId)) {
                            System.out.println("[JwtAuthFilter] Token rejected - user account deleted: " + subject);
                            filterChain.doFilter(request, response);
                            return;
                        }
                    } catch (IllegalArgumentException ignored) {}

                    // Default to USER if role is missing; normalize to uppercase (hasRole('USER') expects ROLE_USER)
                    String effectiveRole = (role != null && !role.isBlank()) ? role.toUpperCase() : "USER";
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + effectiveRole));
                    
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            new User(subject, "", authorities), null, authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    
                    System.out.println("[JwtAuthFilter] Authentication successful - User: " + subject + ", Authorities: " + authorities);
                } else {
                    System.out.println("[JwtAuthFilter] Token rejected - no subject");
                }
            } catch (Exception e) {
                System.out.println("[JwtAuthFilter] Token validation failed: " + e.getMessage());
            }
        } else {
            System.out.println("[JwtAuthFilter] " + method + " " + requestUri + " - No Authorization header or invalid format");
        }
        filterChain.doFilter(request, response);
    }
}


