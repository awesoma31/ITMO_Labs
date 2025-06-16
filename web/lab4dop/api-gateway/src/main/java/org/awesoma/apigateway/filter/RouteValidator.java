package org.awesoma.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> ALLOWED_ROUTES = List.of(
            "/auth/refresh",
            "/auth/register",
            "/auth/login",
            "/eureka"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> ALLOWED_ROUTES
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
