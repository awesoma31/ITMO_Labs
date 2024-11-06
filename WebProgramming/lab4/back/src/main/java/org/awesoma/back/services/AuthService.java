package org.awesoma.back.services;

import jakarta.transaction.Transactional;
import org.awesoma.back.model.User;
import org.awesoma.back.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

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
    }

    @Transactional
    public Map<String, String> login(String username, String password) {
        Optional<User> user = userRepository.getByUsername(username);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String accessToken = tokenService.generateToken(user.get().getId(), user.get().getUsername());
        String refreshToken = tokenService.generateRefreshToken(user.get().getId(), user.get().getUsername());
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    public Optional<Optional<User>> getByUsername(String username) {
        return Optional.ofNullable(userRepository.getByUsername(username));
    }

    private String encrypt(String s) {
        return passwordEncoder.encode(s);
    }
}
