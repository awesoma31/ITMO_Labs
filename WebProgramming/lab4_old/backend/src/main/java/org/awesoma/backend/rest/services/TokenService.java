package org.awesoma.backend.rest.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ejb.Stateless;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Stateless
public class TokenService {
    private static final String key = "SUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYATSUKABLYAT";
    private static final long ACCESS_TOKEN_VALIDITY = 3600000; // 1 hour
    private static final long REFRESH_TOKEN_VALIDITY = 604800000; // 7 days

    public String generateAccessToken(String subject) {
        return generateToken(subject, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, REFRESH_TOKEN_VALIDITY);
    }

    public String generateToken(String subject, long validity) {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + validity;
        Date now = new Date(nowMillis);
        Date exp = new Date(expMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(key.getBytes()))
                .compact();
    }

    public Claims validateToken(String token) {
        Key k = Keys.hmacShaKeyFor(key.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(k)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<String> verify(String token) {
        try {
            Key k = Keys.hmacShaKeyFor(key.getBytes());

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(k)
                    .build()
                    .parseClaimsJws(token);

            return Optional.of(claimsJws.getBody().getSubject());
        } catch (JwtException e) {
            return Optional.empty();
        }
    }
}
