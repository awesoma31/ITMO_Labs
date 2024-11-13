package org.awesoma.back.controllers;

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
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error: ", e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping(value = "refresh", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("trying refresh tokens");
        var rt = refreshTokenRequest.getRefreshToken();
        var newTokens = authService.refreshTokens(rt);


        var response = new ResponseEntity<>(newTokens, HttpStatus.OK);

        log.info(response.toString());

        return response;
    }

    @GetMapping("/test/token")
    public String testToken() {

        return "token valid";
    }

    @Getter
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}
