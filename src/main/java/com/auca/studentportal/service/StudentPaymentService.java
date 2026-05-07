package com.auca.studentportal.service;

import com.auca.studentportal.client.FinanceApiClient;
import com.auca.studentportal.dto.*;
import com.auca.studentportal.exception.AucaApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentPaymentService {

    private final FinanceApiClient financeApiClient;

    public PagedResponse<StudentPaymentResponse> getMyPayments(
            String studentId, int page, int size, String sort) {

        log.info("Fetching student payments for student {} — page: {}, size: {}", studentId, page, size);
        PagedResponse<StudentPaymentResponse> result =
                financeApiClient.getMyPayments(studentId, page, size, sort);
        validateResponse(result, "payments");
        return result;
    }

    public PagedResponse<Object> getMyFees(
            String studentId, int page, int size, String sort) {

        log.info("Fetching student fees for student {} — page: {}, size: {}", studentId, page, size);
        PagedResponse<Object> result =
                financeApiClient.getMyFees(studentId, page, size, sort);
        validateResponse(result, "fees");
        return result;
    }

    public BalanceResponse getMyBalance(String studentId) {
        log.info("Fetching student balance for student {}", studentId);
        BalanceResponse result = financeApiClient.getMyBalance(studentId);
        if (result == null) {
            throw new AucaApiException(
                    "Empty response from Finance API for balance", HttpStatus.BAD_GATEWAY);
        }
        return result;
    }

    private void validateResponse(Object result, String resource) {
        if (result == null) {
            throw new AucaApiException(
                    "Empty response from Finance API for " + resource, HttpStatus.BAD_GATEWAY);
        }
    }
}
