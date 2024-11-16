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
import java.util.ArrayList;
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
            log.info("Method: {}", request.getMethod());
            log.info("URI: {}", request.getRequestURI());
            log.info("Authorization: {}", request.getHeader("Authorization"));
            String token = extractJwtFromRequest(request);

            log.info("before checking token validity");
            if (
                    StringUtils.hasText(token) &&
                            tokenService.valid(token)
//                            && SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                log.info("after checking token validity");
                log.info("token is presented and valid");
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    log.info(SecurityContextHolder.getContext().getAuthentication().toString());
                } else {
                    log.info("no authentication found in context");
                }
                TokenService.JwtUser jwtUser = tokenService.getJwtUserFromToken(token);

                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        jwtUser, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                authentication.setAuthenticated(true);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("after setting authentication: {}", SecurityContextHolder.getContext().getAuthentication().toString());
                log.info("-----------------------------------");
                log.info("-----JWT REQUEST FILTER PASSED-----");
                log.info("-----------------------------------");
            } else {
                log.error("Either token is not presented or it is invalid");
                SecurityContextHolder.clearContext();

//                return;
            }
        } catch (AccessDeniedException e) {
            log.error("{} Request for: {}", request.getMethod(), request.getRequestURI());
            log.error("Access denied: {}", e.getMessage());
//            return;
        }

        catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
//            return;
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
}
