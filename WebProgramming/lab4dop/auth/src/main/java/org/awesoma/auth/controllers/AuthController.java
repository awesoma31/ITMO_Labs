package org.awesoma.auth.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.auth.exception.UserAlreadyExistsException;
import org.awesoma.auth.model.UserPOJO;
import org.awesoma.auth.repository.dto.UserDTO;
import org.awesoma.auth.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        try {
            var username = user.getUsername();
            var password = user.getPassword();
            log.info("username: {}", username);

            authService.register(username, password);

            log.info("user {} registered", username);
            return new ResponseEntity<>("User registered", HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal error: {}", e.getMessage());
            return new ResponseEntity<>("Internal unhandled Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try {
            var username = user.getUsername();
            var password = user.getPassword();
            log.info("login: \n\tusername: {}, \n\tpassword: {}", username, password);

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

    @GetMapping(value = "/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPOJO> authorize(@RequestHeader("Authorization") String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            var token = bearer.substring(7);

            var jwtUser = authService.parseJwtUser(token);
            if (jwtUser.isEmpty()) {
                log.error("JWT user not found");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            var user = authService.userFromJwtUser(jwtUser.get());
            if (user.isEmpty()) {
                log.error("User from jwtUser not found: {}", jwtUser.get());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            return ResponseEntity.ok(UserPOJO.fromUser(user.get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("request refresh tokens");
        var rt = refreshTokenRequest.getRefreshToken();
        List<String> newTokens = null;
        try {
            newTokens = authService.refreshTokens(rt);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return new ResponseEntity<>(newTokens, HttpStatus.OK);
    }

    @Getter
    @Setter
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}
