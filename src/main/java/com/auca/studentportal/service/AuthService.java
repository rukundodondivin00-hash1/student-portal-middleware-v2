package com.auca.studentportal.service;

import com.auca.studentportal.config.AucaApiProperties;
import com.auca.studentportal.cookie.CookieManager;
import com.auca.studentportal.dto.SignInRequest;
import com.auca.studentportal.exception.AucaApiException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final AucaApiProperties props;
    private final CookieManager cookieManager;

    @Lazy
    private AuthService self; // self-proxy for async method invocation

    private static final String SIGNIN_PATH  = "/api/v1/common/auth/signin";
    private static final String REFRESH_PATH = "/api/v1/common/auth/refresh";

    /**
     * Attempt to sign in asynchronously after startup.
     * Non-blocking — application starts even if auth fails.
     */
    @PostConstruct
    public void initializeAuth() {
        log.info("Scheduling async authentication with AUCA...");
        self.attemptSignInAsync(); // invoke via Spring proxy
    }

    /**
     * Async sign-in attempt with retry logic.
     * Non-blocking startup; retries if connection fails.
     */
    @Async
    public void attemptSignInAsync() {
        try {
            signIn();
            log.info("Async authentication succeeded");
        } catch (ResourceAccessException e) {
            log.warn("Initial auth attempt failed (connection timeout/error): {}. Will retry on scheduled refresh.", e.getMessage());
        } catch (Exception e) {
            log.warn("Initial auth attempt failed: {}. Will retry on scheduled refresh.", e.getMessage());
        }
    }

    /**
     * Refresh the access token every 14 minutes.
     * (Assumes access token TTL is 15 minutes — adjust if different.)
     */
    @Scheduled(fixedRateString = "${auca.auth.refresh-interval-ms:840000}")
    public void scheduledTokenRefresh() {
        log.info("Scheduled token refresh triggered");
        try {
            refresh();
        } catch (Exception e) {
            log.warn("Token refresh failed, attempting full re-login: {}", e.getMessage());
            try {
                signIn();
            } catch (Exception ex) {
                log.error("Re-login also failed: {}", ex.getMessage());
            }
        }
    }

    /**
     * Full login with username + password.
     */
    public void signIn() {
        signIn(props.getServiceUsername(), props.getServicePassword());
    }

    /**
     * Full login with given username + password.
     */
    public void signIn(String username, String password) {
        try {
            SignInRequest body = new SignInRequest();
            body.setUsername(username);
            body.setPassword(password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Object> response = restTemplate.exchange(
                    props.getBaseUrl() + SIGNIN_PATH,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    Object.class
            );

            extractAndStoreCookies(response);
            log.info("User {} signed in successfully", username);

        } catch (HttpClientErrorException e) {
            log.error("Sign-in failed for user {}: {} — check credentials", username, e.getMessage());
            throw new AucaApiException("Middleware authentication failed", HttpStatus.UNAUTHORIZED, e);
        } catch (ResourceAccessException e) {
            log.error("Sign-in failed for user {}: Network/timeout error — {}", username, e.getMessage());
            throw new AucaApiException("Middleware authentication failed — connection error", HttpStatus.SERVICE_UNAVAILABLE, e);
        }
    }

    /**
     * Refresh using stored refresh_token cookie.
     */
    public void refresh() {
        String refreshToken = cookieManager.getRefreshToken();
        if (refreshToken.isEmpty()) {
            log.warn("No refresh token available — falling back to full sign-in");
            signIn();
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "refresh_token=" + refreshToken);

        ResponseEntity<Object> response = restTemplate.exchange(
                props.getBaseUrl() + REFRESH_PATH,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Object.class
        );

        extractAndStoreCookies(response);
        log.info("Access token refreshed successfully");
    }

    private void extractAndStoreCookies(ResponseEntity<?> response) {
        List<String> setCookieHeaders = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookieHeaders != null && !setCookieHeaders.isEmpty()) {
            cookieManager.updateCookies(setCookieHeaders);
        } else {
            log.warn("No Set-Cookie headers found in AUCA response");
        }
    }
}
