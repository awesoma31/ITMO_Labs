package org.awesoma.points.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.points.exceptions.ForbiddenException;
import org.awesoma.points.model.BigUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RequestAuthorizationFilter extends OncePerRequestFilter {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081")
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractJwtFromRequest(request);

            if (StringUtils.hasText(token)) {
                BigUser bigUser = webClient.post()
                        .uri("/auth/authorize")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(BigUser.class)
                        .block();

//            var bigUser = createAuthorizeRequest(extractJwtFromRequest(request));
                if (bigUser != null) {
                    List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    var authentication = new UsernamePasswordAuthenticationToken(bigUser, null, authorities);
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    SecurityContextHolder.clearContext();
                    log.error("Didn't get bigUser from auth service");
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "User not found");
                    return;
//                    throw new RuntimeException("User not found");
                }
            }

        } catch (ForbiddenException e) {
            log.error("Forbidden auth server response", e);
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access is forbidden");
            return;
        } catch (AccessDeniedException e) {
            SecurityContextHolder.clearContext();

            log.error("{} Request for: {}", request.getMethod(), request.getRequestURI());
            log.error("Access denied: {}", e.getMessage());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Access is forbidden");
            return;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            logger.error("Could not set user authentication in security context", ex);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to extract JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        var uri = request.getRequestURI();
        log.info("Request URI: {}", uri);
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization header: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private BigUser createAuthorizeRequest(String token) {
        log.info("trying authorize request with token: {}", token);
        //    @Value("${api.auth.url}")
        String authUrl = "http://localhost:8082/auth/authorize";
        return webClient.post()
                .uri(authUrl)
//                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .bodyValue(token)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.FORBIDDEN), response -> {
                    // Handle 403 Forbidden status code
                    log.error("Forbidden auth server response");
                    return Mono.error(new ForbiddenException("Access is forbidden"));
                })
                .bodyToMono(BigUser.class)
                .block();
    }
}
