package com.cryptoterm.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, RateLimitFilter rateLimitFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // Разрешаем все OPTIONS запросы (CORS preflight)
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                    // Swagger UI и OpenAPI docs (должны быть до других правил)
                    .requestMatchers(
                        "/swagger-ui/**",      // Swagger UI (включая статику)
                        "/swagger-ui.html",    // Главная страница Swagger
                        "/v3/api-docs/**",     // OpenAPI документация
                        "/swagger-resources/**",
                        "/webjars/**"          // JavaScript/CSS зависимости
                    ).permitAll()
                    // Публичные API endpoints
                    .requestMatchers("/api/auth/**", "/actuator/**", "/api/device-auth/**").permitAll()
                    // Статические ресурсы приложения (НЕ swagger)
                    .requestMatchers(
                            "/static/**", "/css/**", "/js/**", "/images/**",
                            "/assets/**"
                    ).permitAll()
                    // Все остальное требует аутентификации (включая /api/v1/commands)
                    .anyRequest().authenticated()
            )
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Конфигурация CORS для фронтенда - применяется ко всем /api endpoints
        // Должна быть первой для правильной обработки
        CorsConfiguration apiConfig = new CorsConfiguration();
        apiConfig.setAllowedOriginPatterns(Arrays.asList(
            "http://cryptoterm.ru",
            "https://cryptoterm.ru",
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:8080"
        ));
        apiConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        apiConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        apiConfig.setExposedHeaders(Arrays.asList("Authorization")); // Разрешаем чтение заголовка Authorization из ответа
        apiConfig.setAllowCredentials(true);
        apiConfig.setMaxAge(3600L);
        // Регистрируем для всех API endpoints
        source.registerCorsConfiguration("/api/**", apiConfig);
        
        // Конфигурация CORS для Swagger UI (публичная документация API)
        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        swaggerConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        swaggerConfig.setAllowedHeaders(Arrays.asList("*"));
        swaggerConfig.setAllowCredentials(false);
        swaggerConfig.setMaxAge(3600L);
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig);
        source.registerCorsConfiguration("/v3/api-docs/**", swaggerConfig);
        source.registerCorsConfiguration("/swagger-resources/**", swaggerConfig);
        source.registerCorsConfiguration("/webjars/**", swaggerConfig);
        
        // Fallback конфигурация для всех остальных путей
        CorsConfiguration defaultConfig = new CorsConfiguration();
        defaultConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        defaultConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        defaultConfig.setAllowedHeaders(Arrays.asList("*"));
        defaultConfig.setAllowCredentials(false);
        defaultConfig.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", defaultConfig);
        
        return source;
    }
}


