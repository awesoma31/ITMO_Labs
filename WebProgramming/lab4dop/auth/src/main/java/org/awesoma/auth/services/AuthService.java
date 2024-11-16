package org.awesoma.auth.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.auth.model.User;
import org.awesoma.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public Optional<User> getByUsername(String username) {
        return userRepository.getByUsername(username);
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

    public TokenService.JwtUser extractJwtUserFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            var token = bearerToken.substring(7);
            log.info("token: {}", token);
            return tokenService.getJwtUserFromToken(token);
        }
        log.error("No JWT token found in request");
        throw new IllegalArgumentException("No JWT token found in request");
    }

    public Optional<User> getUser(HttpServletRequest request) {
        var username = extractJwtUserFromRequest(request).getUsername();
        if (StringUtils.hasText(username)) {
            return userRepository.getByUsername(username);
        }
        return Optional.empty();
    }

    public Optional<User> getUserFromJwtUser(TokenService.JwtUser jwtUser) {
        if (StringUtils.hasText(jwtUser.getUsername())) {
            return userRepository.getByUsername(jwtUser.getUsername());
        }
        return Optional.empty();
    }
}
