package com.auca.studentportal.controller;

import com.auca.studentportal.dto.*;
import com.auca.studentportal.service.StudentPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Student Payments", description = "Student payment endpoints — requires AUCA session cookie")
public class StudentPaymentController {

    private final StudentPaymentService studentPaymentService;

    @Operation(summary = "Get my payment history",
            description = "Returns paginated list of payments for the authenticated student. Pass AUCA cookies in Cookie header.")
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<PagedResponse<StudentPaymentResponse>>> getMyPayments(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt:desc") String sort) {

        String cookieHeader = extractCookieHeader(request);
        PagedResponse<StudentPaymentResponse> payments =
                studentPaymentService.getMyPayments(cookieHeader, page, size, sort);
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }

    @Operation(summary = "Get my registration fees",
            description = "Returns paginated list of registration fees per term for the authenticated student.")
    @GetMapping("/fees")
    public ResponseEntity<ApiResponse<PagedResponse<Object>>> getMyFees(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt:desc") String sort) {

        String cookieHeader = extractCookieHeader(request);
        PagedResponse<Object> fees =
                studentPaymentService.getMyFees(cookieHeader, page, size, sort);
        return ResponseEntity.ok(ApiResponse.ok(fees));
    }

    @Operation(summary = "Get my current balance",
            description = "Returns the current financial balance for the authenticated student.")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getMyBalance(HttpServletRequest request) {
        String cookieHeader = extractCookieHeader(request);
        BalanceResponse balance = studentPaymentService.getMyBalance(cookieHeader);
        return ResponseEntity.ok(ApiResponse.ok(balance));
    }

    /**
     * Extract the full Cookie header from the incoming request
     * and forward it as-is to the Finance API.
     */
    private String extractCookieHeader(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null || cookieHeader.isBlank()) {
            log.warn("Request arrived with no Cookie header — AUCA will likely reject it");
        }
        return cookieHeader;
    }
}

    @Operation(summary = "Get my registration fees",
               description = "Returns paginated list of registration fees per term for the authenticated student.")
    @GetMapping("/fees")
    public ResponseEntity<ApiResponse<PagedResponse<Object>>> getMyFees(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt:desc") String sort) {

        String cookieHeader = extractCookieHeader(request);
        PagedResponse<Object> fees =
                studentPaymentService.getMyFees(cookieHeader, page, size, sort);
        return ResponseEntity.ok(ApiResponse.ok(fees));
    }

    @Operation(summary = "Get my current balance",
               description = "Returns the current financial balance for the authenticated student.")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getMyBalance(HttpServletRequest request) {
        String cookieHeader = extractCookieHeader(request);
        BalanceResponse balance = studentPaymentService.getMyBalance(cookieHeader);
        return ResponseEntity.ok(ApiResponse.ok(balance));
    }

    @Operation(summary = "Initiate a payment",
               description = "Starts a new payment process for the authenticated student.")
    @PostMapping("/payments/initiate")
    public ResponseEntity<ApiResponse<StudentPaymentResponse>> initiatePayment(
            HttpServletRequest request,
            @Valid @RequestBody PaymentInitiateRequest body) {

        String cookieHeader = extractCookieHeader(request);
        StudentPaymentResponse result = studentPaymentService.initiatePayment(cookieHeader, body);
        return ResponseEntity.ok(ApiResponse.ok("Payment initiated successfully", result));
    }

    /**
     * Extract the full Cookie header from the incoming request
     * and forward it as-is to the Finance API.
     */
    private String extractCookieHeader(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null || cookieHeader.isBlank()) {
            log.warn("Request arrived with no Cookie header — AUCA will likely reject it");
        }
        return cookieHeader;
    }
}
