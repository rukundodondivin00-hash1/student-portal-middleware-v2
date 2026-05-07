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

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Tag(name = "Student Payments", description = "Student payment endpoints — requires AUCA session cookie")
public class StudentPaymentController {

    private final StudentPaymentService studentPaymentService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Get my payment history",
            description = "Returns paginated list of payments for the authenticated student. " +
                    "Pass AUCA access_token cookie from portal login.")
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
                    "Pass AUCA access_token cookie from portal login.")
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
                    "Pass AUCA access_token cookie from portal login.")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getMyBalance(HttpServletRequest request) {
        String studentId = extractStudentId(request);
        BalanceResponse balance = studentPaymentService.getMyBalance(studentId);
        return ResponseEntity.ok(ApiResponse.ok(balance));
    }

    /**
     * Extract studentId from the AUCA access_token cookie.
     * The cookie should be in the format: access_token=<JWT_TOKEN>
     *
     * @throws AucaApiException if cookie missing or invalid
     */
    private String extractStudentId(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null || cookieHeader.isBlank()) {
            log.warn("Request missing Cookie header");
            throw new AucaApiException("Missing Cookie header. Please include AUCA access_token cookie.",
                    HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtUtil.extractTokenFromCookie(cookieHeader);
        if (accessToken == null) {
            log.warn("No access_token found in Cookie header");
            throw new AucaApiException("Missing access_token cookie. Please log into AUCA portal first.",
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
