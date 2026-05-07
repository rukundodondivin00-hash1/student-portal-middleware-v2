package com.auca.studentportal.client;

import com.auca.studentportal.dto.*;

public interface FinanceApiClient {

    // ── Student-facing (service account calls AUCA Finance with studentId) ─────
    PagedResponse<StudentPaymentResponse> getMyPayments(String studentId, int page, int size, String sort);
    PagedResponse<Object> getMyFees(String studentId, int page, int size, String sort);
    BalanceResponse getMyBalance(String studentId);

    // ── Webhook (service account cookie needed) ───────────────────────────────
    void forwardNotification(FinanceNotificationRequest request, String serviceCookieHeader);
}
