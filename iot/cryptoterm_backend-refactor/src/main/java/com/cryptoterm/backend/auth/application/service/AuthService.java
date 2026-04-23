package com.cryptoterm.backend.service;

import com.cryptoterm.backend.auth.domain.User;
import com.cryptoterm.backend.command.application.port.out.AsicCommandRepository;
import com.cryptoterm.backend.device.application.port.out.DeviceRepository;
import com.cryptoterm.backend.device.domain.Device;
import com.cryptoterm.backend.dto.AuthResponse;
import com.cryptoterm.backend.auth.application.port.out.UserRepository;
import com.cryptoterm.backend.security.DeletedUserStore;
import com.cryptoterm.backend.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final AsicCommandRepository asicCommandRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final DeletedUserStore deletedUserStore;

    public AuthService(UserRepository userRepository,
                       DeviceRepository deviceRepository,
                       AsicCommandRepository asicCommandRepository,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       DeletedUserStore deletedUserStore) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.asicCommandRepository = asicCommandRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.deletedUserStore = deletedUserStore;
    }

    @Transactional
    public AuthResponse register(String username, String email, String password, String telegram) {
        // Registration is only allowed for pre-registered users (created via device registration)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Registration failed: No pre-registered user found with email: {}", email);
                    return new IllegalArgumentException("User not found. Registration is only available for devices that have been pre-registered.");
                });
        
        // If user already has a password, registration is complete - reject
        if (user.getPasswordHash() != null && !user.getPasswordHash().isBlank()) {
            log.warn("Registration failed: User with email {} is already fully registered", email);
            throw new IllegalArgumentException("User with email " + email + " is already registered. Please use login.");
        }
        
        // Check if username is taken
        if (username != null && !username.isBlank() && userRepository.existsByUsername(username)) {
            log.warn("Registration failed: Username already exists");
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }
        
        // Pre-registered user exists - complete the registration
        log.info("Completing registration for pre-registered user with email: {}", email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        if (telegram != null && !telegram.isBlank()) {
            user.setTelegram(telegram);
        }
        user.setRole("USER");

        User newUser = user;

        // Используем username или "anonymous" в качестве fallback для генерации токена
        String usernameForToken = username != null && !username.isBlank() 
                ? username 
                : "anonymous";

        String accessToken = jwtService.generateAccessToken(newUser.getId(), usernameForToken, newUser.getEmail(), newUser.getRole());
        String refreshToken = jwtService.generateRefreshToken(newUser.getId());
        
        newUser.setRefreshToken(refreshToken);
        newUser.setRefreshTokenExpiry(OffsetDateTime.now(ZoneOffset.UTC)
                .plusSeconds(jwtService.getRefreshTokenExpirationSeconds()));

        newUser = userRepository.save(newUser);
        
        log.info("User registered successfully: {}", newUser.getId());
        
        return new AuthResponse(accessToken, refreshToken, newUser.getId(), 
                username != null && !username.isBlank() ? username : "anonymous", 
                newUser.getEmail());
    }

    @Transactional
    public AuthResponse login(String usernameOrEmail, String password) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    log.warn("Login attempt failed: User not found");
                    return new IllegalArgumentException("User not found");
                });
        
        // SECURITY: Prevent login to pre-registered accounts without password
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            log.warn("Login attempt failed: User {} is pre-registered and has no password set. Registration required.", user.getId());
            throw new IllegalArgumentException("Account is not fully registered. Please complete registration first.");
        }
        
        // Validate password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Login attempt failed: Invalid password for user ID: {}", user.getId());
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        log.info("User logged in successfully: {}", user.getId());

        // Используем username или "anonymous" в качестве fallback для генерации токена
        String usernameForToken = user.getUsername() != null && !user.getUsername().isBlank() 
                ? user.getUsername() 
                : "anonymous";
        
        String role = (user.getRole() != null && !user.getRole().isBlank()) ? user.getRole() : "USER";
        String accessToken = jwtService.generateAccessToken(user.getId(), usernameForToken, user.getEmail(), role);
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(OffsetDateTime.now(ZoneOffset.UTC)
                .plusSeconds(jwtService.getRefreshTokenExpirationSeconds()));

        user = userRepository.save(user);
        
        return new AuthResponse(accessToken, refreshToken, user.getId(), 
                user.getUsername() != null && !user.getUsername().isBlank() ? user.getUsername() : "anonymous", 
                user.getEmail());
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            log.warn("Token refresh failed: Token expired");
            throw new IllegalArgumentException("Refresh token expired");
        }

        UUID userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Token refresh failed: User not found");
                    return new IllegalArgumentException("User not found");
                });

        if (!refreshToken.equals(user.getRefreshToken())) {
            log.warn("Token refresh failed: Invalid refresh token for user ID: {}", userId);
            throw new IllegalArgumentException("Invalid refresh token");
        }

        if (user.getRefreshTokenExpiry() == null || 
                user.getRefreshTokenExpiry().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            log.warn("Token refresh failed: Token expired in DB for user ID: {}", userId);
            throw new IllegalArgumentException("Refresh token expired");
        }
        
        log.info("Token refreshed successfully for user: {}", userId);

        // Используем username или "anonymous" в качестве fallback для генерации токена
        String usernameForToken = user.getUsername() != null && !user.getUsername().isBlank() 
                ? user.getUsername() 
                : "anonymous";

        String role = (user.getRole() != null && !user.getRole().isBlank()) ? user.getRole() : "USER";
        String newAccessToken = jwtService.generateAccessToken(user.getId(), usernameForToken, user.getEmail(), role);
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());
        
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(OffsetDateTime.now(ZoneOffset.UTC)
                .plusSeconds(jwtService.getRefreshTokenExpirationSeconds()));

        user = userRepository.save(user);
        
        return new AuthResponse(newAccessToken, newRefreshToken, user.getId(), 
                user.getUsername() != null && !user.getUsername().isBlank() ? user.getUsername() : "anonymous", 
                user.getEmail());
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
        
        log.info("User logged out successfully: {}", userId);
    }

    @Transactional
    public void deleteAccount(UUID userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Account is not fully registered");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Delete commands from MongoDB (not covered by PostgreSQL CASCADE)
        List<Device> devices = deviceRepository.findByOwner_Id(userId);
        if (!devices.isEmpty()) {
            List<String> deviceIds = devices.stream()
                    .map(d -> d.getId().toString())
                    .collect(Collectors.toList());
            asicCommandRepository.deleteByDeviceIdIn(deviceIds);
            log.info("Deleted MongoDB commands for {} device(s) of user {}", deviceIds.size(), userId);
        }

        // Delete user from PostgreSQL (CASCADE removes devices, miners, metrics, logs, sensors, conditions)
        userRepository.delete(user);

        // Invalidate any existing access tokens
        deletedUserStore.markDeleted(userId);

        log.info("Account deleted for user {}", userId);
    }
}


