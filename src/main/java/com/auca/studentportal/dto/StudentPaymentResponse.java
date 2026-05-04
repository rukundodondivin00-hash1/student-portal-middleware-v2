package com.auca.studentportal.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class StudentPaymentResponse {
    private String id;
    private String referenceId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String aucaRequestId;
    private String urubutoTransactionId;
    private String paymentMethod;
    private String description;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
    private String payerCode;
    private String payerNames;
    private String phoneNumber;
    private String channelName;
    private String cardProcessingUrl;
    private String slipNumber;
    private String feeType;
}
