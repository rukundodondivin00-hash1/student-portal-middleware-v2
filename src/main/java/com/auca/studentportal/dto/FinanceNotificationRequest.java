package com.auca.studentportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class FinanceNotificationRequest {
    @NotBlank(message = "referenceId is required")
    private String referenceId;
    @NotBlank(message = "status is required")
    private String status;
    @NotNull(message = "amount is required")
    private BigDecimal amount;
    private String currency;
    private String urubutoTransactionId;
    private String payerCode;
    private String payerNames;
    private String phoneNumber;
    private String slipNumber;
    private String feeType;
}
