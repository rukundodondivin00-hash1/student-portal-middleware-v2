package com.auca.studentportal.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class JwtUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract student ID (sub claim) from AUCA JWT access token.
     * Note: This does NOT verify the signature. It only decodes the payload.
     * The token's authenticity is enforced by AUCA Finance API when we call it with service account.
     *
     * @param jwtToken the raw JWT token (without "Bearer " prefix)
     * @return student ID (sub claim) or null if parsing fails
     */
    public String extractStudentId(String jwtToken) {
        if (jwtToken == null || jwtToken.isBlank()) {
            return null;
        }

        try {
            // JWT format: header.payload.signature (3 parts separated by '.')
            String[] parts = jwtToken.split("\\.");
            if (parts.length < 2) {
                log.warn("Invalid JWT format: expected 3 parts, got {}", parts.length);
                return null;
            }

            // Decode payload (second part) using Base64 URL decoder (JWT standard)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Extract "sub" claim (standard JWT subject claim)
            JsonNode subNode = jsonNode.get("sub");
            if (subNode != null && subNode.isValueNode()) {
                String studentId = subNode.asText();
                log.debug("Extracted studentId: {} from JWT", studentId);
                return studentId;
            } else {
                log.warn("JWT payload missing 'sub' claim: {}", payload);
                return null;
            }
        } catch (IllegalArgumentException e) {
            log.warn("Base64 decode failed for JWT: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Failed to parse JWT payload: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extract access token from Cookie header value.
     * Handles two formats:
     *   1. Standard cookie: "access_token=<token>; other=value"
     *   2. Raw token (Swagger direct entry): "<token>"
     *
     * @param cookieHeader the Cookie header value
     * @return the access token, or null if not found/invalid
     */
    public String extractTokenFromCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }

        // Try standard cookie format first: "access_token=xxx"
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String trimmed = cookie.trim();
            if (trimmed.startsWith("access_token=")) {
                return trimmed.substring("access_token=".length());
            }
        }

        // If no access_token= found, treat as raw JWT token (Swagger UI direct paste)
        String trimmed = cookieHeader.trim();
        if (trimmed.split("\\.").length == 3) {
            log.debug("Treating Cookie header as raw JWT token (no access_token= prefix)");
            return trimmed;
        }

        log.warn("Could not extract access_token from Cookie header: {}", cookieHeader);
        return null;
    }
}
