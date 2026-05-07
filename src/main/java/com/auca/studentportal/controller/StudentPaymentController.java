package com.auca.studentportal.controller;

import com.auca.studentportal.dto.ApiResponse;
import com.auca.studentportal.dto.BalanceResponse;
import com.auca.studentportal.dto.PagedResponse;
import com.auca.studentportal.dto.StudentPaymentResponse;
import com.auca.studentportal.exception.AucaApiException;
import com.auca.studentportal.service.StudentPaymentService;
import com.auca.studentportal.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Student Payments", description = "Student payment endpoints — requires AUCA access token")
public class StudentPaymentController {

    private final StudentPaymentService studentPaymentService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Get my payment history",
            description = "Returns paginated list of payments for the authenticated student. " +
                    "Pass AUCA access token via Authorization: Bearer <token> header (Swagger) or Cookie: access_token=<token>.")
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<PagedResponse<StudentPaymentResponse>>> getMyPayments(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt:desc") String sort) {

        String studentId = extractStudentId(request);
        PagedResponse<StudentPaymentResponse> payments =
                studentPaymentService.getMyPayments(studentId, page, size, sort);
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }

    @Operation(summary = "Get my registration fees",
            description = "Returns paginated list of registration fees per term for the authenticated student. " +
                    "Pass AUCA access token via Authorization: Bearer <token> header (Swagger) or Cookie: access_token=<token>.")
    @GetMapping("/fees")
    public ResponseEntity<ApiResponse<PagedResponse<Object>>> getMyFees(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt:desc") String sort) {

        String studentId = extractStudentId(request);
        PagedResponse<Object> fees = studentPaymentService.getMyFees(studentId, page, size, sort);
        return ResponseEntity.ok(ApiResponse.ok(fees));
    }

    @Operation(summary = "Get my current balance",
            description = "Returns the current financial balance for the authenticated student. " +
                    "Pass AUCA access token via Authorization: Bearer <token> header (Swagger) or Cookie: access_token=<token>.")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getMyBalance(HttpServletRequest request) {
        String studentId = extractStudentId(request);
        BalanceResponse balance = studentPaymentService.getMyBalance(studentId);
        return ResponseEntity.ok(ApiResponse.ok(balance));
    }

    /**
     * Extract studentId from the AUCA access_token.
     * Supports two formats:
     *   1. Cookie header: "access_token=<JWT_TOKEN>"
     *   2. Authorization header: "Bearer <JWT_TOKEN>"
     *
     * @throws AucaApiException if token missing or invalid
     */
    private String extractStudentId(HttpServletRequest request) {
        String accessToken = null;

        // Try Authorization header first (works in Swagger UI)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7).trim();
            log.debug("Using token from Authorization header");
        }

        // Fallback to Cookie header
        if (accessToken == null) {
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null && !cookieHeader.isBlank()) {
                accessToken = jwtUtil.extractTokenFromCookie(cookieHeader);
            }
        }

        if (accessToken == null) {
            log.warn("No access_token found in Authorization or Cookie header");
            throw new AucaApiException(
                    "Missing authentication. Provide token in Authorization: Bearer <token> or Cookie: access_token=<token>",
                    HttpStatus.UNAUTHORIZED);
        }

        String studentId = jwtUtil.extractStudentId(accessToken);
        if (studentId == null) {
            log.warn("Failed to extract studentId from access_token");
            throw new AucaApiException("Invalid AUCA access token.", HttpStatus.UNAUTHORIZED);
        }

        log.debug("Authenticated student: {}", studentId);
        return studentId;
    }
}
