package com.auca.studentportal.controller;

import com.auca.studentportal.dto.ApiResponse;
import com.auca.studentportal.dto.FinanceNotificationRequest;
import com.auca.studentportal.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/finance")
@RequiredArgsConstructor
@Tag(name = "Finance Webhook", description = "Receives payment notifications from Urubuto via Finance system")
public class FinanceWebhookController {

    private final WebhookService webhookService;

    @Operation(summary = "Receive payment notification",
               description = "Called by the Finance system when Urubuto processes a payment. No authentication required.")
    @PostMapping("/student-payments/notifications")
    public ResponseEntity<ApiResponse<Void>> receiveNotification(
            @Valid @RequestBody FinanceNotificationRequest request) {

        log.info("Webhook received — referenceId: {}, status: {}",
                request.getReferenceId(), request.getStatus());
        webhookService.processNotification(request);
        return ResponseEntity.ok(ApiResponse.ok("Notification received and forwarded", null));
    }
}
