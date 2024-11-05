package org.awesoma.back.controllers;

import org.awesoma.back.repository.dto.UserDTO;
import org.awesoma.back.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/test")
    public String login() {
        return "auth tested";
    }

    @PostMapping(value = "/reg", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody UserDTO user) {
        var username = user.getUsername();
        var u = authService.getByUsername(username);

        if (u.isPresent()) {
            return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
        }

        try {
            var password = user.getPassword();
            authService.register(username, password);

            return new ResponseEntity<>("User registered", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        try {
            var username = user.getUsername();
            var password = user.getPassword();
            var tokens = authService.login(username, password);
            return new ResponseEntity<>(tokens, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test/token")
    public String testToken() {

        return "token valid";
    }
}
