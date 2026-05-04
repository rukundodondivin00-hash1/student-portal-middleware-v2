package com.auca.studentportal.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BalanceResponse {
    private String studentId;
    private BigDecimal balance;
}
