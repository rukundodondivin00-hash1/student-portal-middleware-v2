package com.auca.studentportal.client;

import com.auca.studentportal.dto.*;

public interface FinanceApiClient {
    PagedResponse<StudentPaymentResponse> getMyPayments(String studentId, int page, int size, String sort);
    PagedResponse<Object> getMyFees(String studentId, int page, int size, String sort);
    BalanceResponse getMyBalance(String studentId);
    void forwardNotification(FinanceNotificationRequest request);
}
