package org.awesoma.points.services;

import jakarta.servlet.http.HttpServletRequest;
import org.awesoma.points.model.BigUser;
import org.awesoma.points.util.JwtUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ContextService {

    public static String getTokenFromContext() {
        JwtUser jwtUser = getUserFromContext().getJwtUser();
        return jwtUser.getAccessToken();
    }

    public static BigUser getUserFromContext()  {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        return (BigUser) authentication.getPrincipal();
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        var bigUser = getUserFromContext();
        return bigUser.getUser().getId();
    }
}
