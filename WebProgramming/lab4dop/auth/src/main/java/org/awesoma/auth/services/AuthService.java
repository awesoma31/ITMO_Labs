package org.awesoma.auth.services;

import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.awesoma.auth.exception.UserAlreadyExistsException;
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
    private final UserRepository ur;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository ur, TokenService tokenService) {
        this.ur = ur;
        this.tokenService = tokenService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public void register(String username, String password) throws UserAlreadyExistsException {
        var user = ur.getByUsername(username);
        if (user.isPresent()) {
            log.error("User already exists");
            throw new UserAlreadyExistsException("User already exists");
        }
        ur.saveAndFlush(new User(username, encode(password)));
        log.info("user saved in DB");
    }

    @Transactional
    public Map<String, String> login(String username, String password) throws LoginException {
        var user = ur.getByUsername(username);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            log.error("Invalid username or password");
            throw new LoginException("Invalid username or password");
        }

        var accessToken = tokenService.generateToken(user.get().getId(), user.get().getUsername());
        var refreshToken = tokenService.generateRefreshToken(user.get().getId(), user.get().getUsername());
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    private String encode(String s) {
        return passwordEncoder.encode(s);
    }

    public List<String> refreshTokens(String refreshToken) throws IllegalArgumentException {
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

    public Optional<TokenService.JwtUser> parseJwtUser(String bearerToken) {
        try {
            return Optional.of(tokenService.getJwtUserFromToken(bearerToken));
        } catch (JwtException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<User> userFromJwtUser(TokenService.JwtUser jwtUser) {
        if (StringUtils.hasText(jwtUser.getUsername())) {
            return ur.getByUsername(jwtUser.getUsername());
        }
        return Optional.empty();
    }
}
