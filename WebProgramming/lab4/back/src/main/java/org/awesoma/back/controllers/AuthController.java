package org.awesoma.back.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.model.User;
import org.awesoma.back.repository.dto.UserDTO;
import org.awesoma.back.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test() {
        return "auth tested";
    }


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "User already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        log.info("trying register");
       var username = user.getUsername();
        log.info("username: {}", username);
       Optional<User> u = authService.getByUsername(username);

        if (u.isPresent()) {
            log.warn("user already exist, aborting register");
            return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
        }

        log.info("user found");

        try {
            var password = user.getPassword();
            authService.register(username, password);

            log.info("user {} registered", username);
            return new ResponseEntity<>("User registered", HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Internal error: {}", e.getMessage());
            return new ResponseEntity<>("Internal Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login a user and provide access tokens")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated and returned tokens")
    @ApiResponse(responseCode = "403", description = "Login denied")
    @ApiResponse(responseCode = "400", description = "Bad request due to incorrect input")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try {
            var username = user.getUsername();
            var password = user.getPassword();
            log.info("trying login: \n\tusername: {}, \n\tpassword: {}", username, password);

            var tokens = authService.login(username, password);
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch (LoginException e) {
            log.info("Login denied: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Login error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Refresh the access and refresh tokens")
    @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully")
    public ResponseEntity<List<String>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("trying refresh tokens");
        var rt = refreshTokenRequest.getRefreshToken();
        var newTokens = authService.refreshTokens(rt);


        var response = new ResponseEntity<>(newTokens, HttpStatus.OK);

        log.info(response.toString());

        return response;
    }

    @Getter
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}
