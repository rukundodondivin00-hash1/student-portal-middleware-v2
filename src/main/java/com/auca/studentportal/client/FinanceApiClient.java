package com.auca.studentportal.client;

import com.auca.studentportal.dto.*;

public interface FinanceApiClient {

    // ── Student-facing (forward student cookies) ──────────────────────────
    PagedResponse<StudentPaymentResponse> getMyPayments(String cookieHeader, int page, int size, String sort);
    PagedResponse<Object> getMyFees(String cookieHeader, int page, int size, String sort);
    BalanceResponse getMyBalance(String cookieHeader);

    // ── Webhook (no student cookie needed) ───────────────────────────────
    void forwardNotification(FinanceNotificationRequest request, String serviceCookieHeader);
}
