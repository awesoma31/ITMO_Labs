package org.awesoma.back.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

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
     * @param userId   The ID of the user.
     * @param username The username of the user.
     * @return A signed JWT token.
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
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

        Long userId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);

        return new JwtUser(userId, username);
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
            log.error("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired.");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getUserTypeFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("usertype").toString();
    }

    public record JwtUser(Long id, String username) {
    }
}
