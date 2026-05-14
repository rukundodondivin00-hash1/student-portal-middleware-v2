package com.auca.studentportal.service;

import com.auca.studentportal.client.FinanceApiClient;
import com.auca.studentportal.dto.FinanceNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final FinanceApiClient financeApiClient;

    public void processNotification(FinanceNotificationRequest request) {
        log.info("Processing payment notification — referenceId: {}, status: {}",
                request.getReferenceId(), request.getStatus());
        financeApiClient.forwardNotification(request);
        log.info("Notification forwarded successfully for referenceId: {}", request.getReferenceId());
    }
}