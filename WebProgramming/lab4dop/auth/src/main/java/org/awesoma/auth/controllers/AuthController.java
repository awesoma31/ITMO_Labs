package org.awesoma.auth.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.auth.model.BigUser;
import org.awesoma.auth.model.UserPOJO;
import org.awesoma.auth.model.User;
import org.awesoma.auth.repository.dto.UserDTO;
import org.awesoma.auth.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.StringUtils;
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

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        log.info("request register");
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

    @GetMapping(value = "/authorize", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPOJO> authorize(@RequestHeader("Authorization") String bearer) {
        try {
            if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
                var token = bearer.substring(7);

                var jwtUser = authService.extractJwtUserFromToken(token);
                if (jwtUser.isEmpty()) {
                    log.error("JWT user not found");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }

                var user = authService.getUserFromJwtUser(jwtUser.get());
                if (user.isEmpty()) {
                    log.error("User from jwtUser not found: {}", jwtUser.get());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
                }
                var bigUser = new BigUser(user.get(), jwtUser.get());
                return ResponseEntity.ok(UserPOJO.fromBigUser(bigUser));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("request refresh tokens");
        var rt = refreshTokenRequest.getRefreshToken();
        var newTokens = authService.refreshTokens(rt);

        var response = new ResponseEntity<>(newTokens, HttpStatus.OK);

        log.info(response.toString());

        return response;
    }

    @Getter
    @Setter
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}