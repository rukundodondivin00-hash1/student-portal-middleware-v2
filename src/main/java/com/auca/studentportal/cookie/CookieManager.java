package com.auca.studentportal.cookie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Stores and manages the session cookies for the middleware's own
 * service account. Thread-safe — the scheduled refresher and
 * incoming requests access this concurrently.
 */
@Slf4j
@Component
public class CookieManager {

    private final CopyOnWriteArrayList<String> serviceCookies = new CopyOnWriteArrayList<>();

    /**
     * Replace stored cookies with a fresh set received after login/refresh.
     */
    public void updateCookies(List<String> newCookies) {
        serviceCookies.clear();
        serviceCookies.addAll(newCookies);
        log.info("Service account cookies updated. Cookie count: {}", newCookies.size());
    }

    /**
     * Returns all stored service cookies as a single Cookie header value.
     * Example: "access_token=abc; refresh_token=xyz"
     */
    public String getServiceCookieHeader() {
        if (serviceCookies.isEmpty()) {
            log.warn("No service cookies available — middleware may not be authenticated yet");
            return "";
        }
        // Each element from Set-Cookie is like: "access_token=abc; Path=/; HttpOnly"
        // We only need the name=value part for the outgoing Cookie header
        StringBuilder sb = new StringBuilder();
        for (String cookie : serviceCookies) {
            String nameValue = cookie.split(";")[0].trim();
            if (!sb.isEmpty()) sb.append("; ");
            sb.append(nameValue);
        }
        return sb.toString();
    }

    /**
     * Extracts and returns the refresh_token value from stored cookies.
     */
    public String getRefreshToken() {
        return serviceCookies.stream()
                .filter(c -> c.startsWith("refresh_token="))
                .map(c -> c.split(";")[0].replace("refresh_token=", "").trim())
                .findFirst()
                .orElse("");
    }

    public boolean hasCookies() {
        return !serviceCookies.isEmpty();
    }

    /**
     * Build a forwarded Cookie header from raw cookie strings passed
     * by the Student Portal to our middleware.
     */
    public static String buildCookieHeader(List<String> rawCookies) {
        if (rawCookies == null || rawCookies.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String cookie : rawCookies) {
            String nameValue = cookie.split(";")[0].trim();
            if (!sb.isEmpty()) sb.append("; ");
            sb.append(nameValue);
        }
        return sb.toString();
    }
}
