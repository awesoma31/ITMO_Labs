package com.cryptoterm.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простой rate limiter для защиты auth эндпоинтов от brute-force атак
 * Ограничение: 10 запросов в минуту на IP адрес
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;
    
    private static class RateLimitInfo {
        private int count;
        private Instant windowStart;
        
        public RateLimitInfo() {
            this.count = 1;
            this.windowStart = Instant.now();
        }
        
        public boolean isAllowed(int maxRequests, long windowSeconds) {
            Instant now = Instant.now();
            
            if (now.getEpochSecond() - windowStart.getEpochSecond() > windowSeconds) {
                count = 1;
                windowStart = now;
                return true;
            }
            
            if (count >= maxRequests) {
                return false;
            }
            
            count++;
            return true;
        }
    }
    
    private final Map<String, RateLimitInfo> rateLimitStore = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip rate limiting if disabled (e.g., in tests)
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String path = request.getRequestURI();
        
        if (shouldRateLimit(path)) {
            String clientId = getClientId(request);
            RateLimitInfo info = rateLimitStore.computeIfAbsent(clientId, k -> new RateLimitInfo());
            
            if (!info.isAllowed(10, 60)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean shouldRateLimit(String path) {
        return path.startsWith("/api/auth/login") || 
               path.startsWith("/api/auth/register") || 
               path.startsWith("/api/auth/refresh");
    }
    
    private String getClientId(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
