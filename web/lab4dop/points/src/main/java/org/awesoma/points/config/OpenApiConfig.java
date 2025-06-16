package org.awesoma.points.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(name = "awesoma", email = "409856@niuitmo.ru"),
                description = "Backend API for Web Lab 4",
                title = "Web Lab 4 API",
                version = "1.0"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local server")
        }
)
@SecurityScheme(
        name = "JWT",
        description = "JWT auth description",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
