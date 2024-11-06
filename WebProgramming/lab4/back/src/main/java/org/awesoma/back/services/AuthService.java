package org.awesoma.back.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.back.model.User;
import org.awesoma.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public void register(String username, String password) {
        userRepository.saveAndFlush(new User(username, encrypt(password)));
        log.info("user saved in DB");
    }

    @Transactional
    public Map<String, String> login(String username, String password) throws LoginException {
        Optional<User> user = userRepository.getByUsername(username);
        if (user.isPresent()) log.info("user found in DB");
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            log.error("Invalid username or password");
            throw new LoginException("Invalid username or password");
//            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = tokenService.generateToken(user.get().getId(), user.get().getUsername());
        String refreshToken = tokenService.generateRefreshToken(user.get().getId(), user.get().getUsername());
        log.info("generated tokens: at - {}, rt - {}", accessToken, refreshToken);
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public Optional<Optional<User>> getByUsername(String username) {
        return Optional.ofNullable(userRepository.getByUsername(username));
    }

    private String encrypt(String s) {
        return passwordEncoder.encode(s);
    }

    public List<String> refreshTokens(String refreshToken) {
        if (!tokenService.valid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var newAT = tokenService.generateToken(
                tokenService.getUserIdFromToken(refreshToken),
                tokenService.getUsernameFromJWT(refreshToken)
        );
        var newRT = tokenService.generateRefreshToken(
                tokenService.getUserIdFromToken(refreshToken),
                tokenService.getUsernameFromJWT(refreshToken)
        );

        return List.of(newAT, newRT);
    }
}
