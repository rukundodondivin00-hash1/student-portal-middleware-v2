package com.auca.studentportal.controller;

import com.auca.studentportal.dto.ApiResponse;
import com.auca.studentportal.dto.UsernamePasswordRequest;
import com.auca.studentportal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/middleware/auth")
@RequiredArgsConstructor
@Tag(name = "Middleware Auth", description = "Internal auth management for the middleware service account")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Sign in with AUCA credentials",
                description = "Authenticates with AUCA using username and password to establish a session.")
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<String>> signIn(@RequestBody UsernamePasswordRequest request) {
        authService.signIn(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.ok("Successfully authenticated with AUCA", null));
    }

    @Operation(summary = "Force re-login",
                description = "Forces the middleware to re-authenticate its service account with AUCA. Useful if cookies expire unexpectedly.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> forceRefresh() {
        authService.signIn();
        return ResponseEntity.ok(ApiResponse.ok("Service account re-authenticated successfully", null));
    }
}
