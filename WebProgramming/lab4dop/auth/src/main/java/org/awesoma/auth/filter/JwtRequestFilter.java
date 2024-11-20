package org.awesoma.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.auth.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("-------------------------------");
            log.info("-----FILTERING NEW REQUEST-----");
            log.info("------------------------------");

            String clientIp = request.getRemoteAddr(); // Client's IP address
            String targetUri = request.getRequestURI(); // Target URI
            log.info("Request from: {}, to: {}", clientIp, targetUri);

            log.info("Method: {}", request.getMethod());
            log.info("Authorization: {}", request.getHeader("Authorization"));
            String token = extractJwtFromRequest(request);

            if (
                    StringUtils.hasText(token) &&
                            tokenService.valid(token)
//                            && SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                log.info("token is presented and valid");
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    log.info("Found security context: {}", SecurityContextHolder.getContext().getAuthentication().toString());
                } else {
                    log.info("No authentication found in context");
                }
                TokenService.JwtUser jwtUser = tokenService.getJwtUserFromToken(token);

                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        jwtUser, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Setting authentication: {}", SecurityContextHolder.getContext().getAuthentication().toString());

                log.info("-----------------------------------");
                log.info("-----JWT REQUEST FILTER PASSED-----");
                log.info("-----------------------------------");
            } else {
                log.error("Either token is not presented or it is invalid");
                SecurityContextHolder.clearContext();
            }
        } catch (AccessDeniedException e) {
            log.error("{} Request for: {}", request.getMethod(), request.getRequestURI());
            log.error("Access denied: {}", e.getMessage());
        }

        catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
