package com.auca.studentportal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI — open for testing
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Health check
                        .requestMatchers("/actuator/health").permitAll()
                        // Auth endpoints — open
                        .requestMatchers("/api/v1/middleware/auth/**").permitAll()
                        // Webhook from Urubuto — open
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/finance/student-payments/notifications").permitAll()
                        // Everything else passes through
                        // (AUCA system handles real auth via cookies)
                        .anyRequest().permitAll()
                )
                .build();
    }
}
