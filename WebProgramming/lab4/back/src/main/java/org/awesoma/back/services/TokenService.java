package org.awesoma.back.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.util.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class TokenService {
    private final Key key;
    private final Long jwtExpirationMs;
    private final Long refreshExpirationMs;

    public TokenService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expirationMs}") Long expirationMs,
            @Value("${jwt.refreshExpirationMs}") Long refreshExpirationMs1
    ) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtExpirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs1;
    }

    /**
     * Generate a JWT token containing user's username and ID as claims.
     *
     * @param id       The ID of the user.
     * @param username The username of the user.
     * @return A signed JWT token.
     */
    public String generateToken(Long id, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("username", username)
                .claim("id", id)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long id, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract user ID and username from the JWT token.
     *
     * @param token The JWT token.
     * @return A JwtUser object containing user details.
     */
    public JwtUser parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long id = claims.get("id", Long.class);
        String username = claims.get("username", String.class);

        return new JwtUser(id, username);
    }

    /**
     * Validate the JWT token.
     *
     * @param authToken The JWT token.
     * @return True if valid, false otherwise.
     */
    public boolean valid(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT token: {}", authToken, e);
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", authToken, e);
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", authToken, e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty or null: {}", authToken, e);
        }
        return false;
    }

    public String getUsernameFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                 io.jsonwebtoken.security.SignatureException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("id", Long.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String getTokenFromContext() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User is not authenticated");
        }

        TokenService.JwtUser jwtUser = (TokenService.JwtUser) authentication.getPrincipal();
        return jwtUser.getAccessToken();
    }

    @Getter
    public static final class JwtUser {
        private final Long id;
        private final String username;
        @Setter
        private String accessToken = null;
        @Setter
        private String refreshToken = null;

        public JwtUser(Long id, String username, String accessToken, String refreshToken) {
            this.id = id;
            this.username = username;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public JwtUser(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (JwtUser) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.username, that.username) &&
                    Objects.equals(this.accessToken, that.accessToken) &&
                    Objects.equals(this.refreshToken, that.refreshToken);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, username, accessToken, refreshToken);
        }

        @Override
        public String toString() {
            return "JwtUser[" +
                    "id=" + id + ", " +
                    "username=" + username + ", " +
                    "accessToken=" + accessToken + ", " +
                    "refreshToken=" + refreshToken + ']';
        }

    }
}
