package com.auca.studentportal;

import com.auca.studentportal.client.FinanceApiClient;
import com.auca.studentportal.dto.*;
import com.auca.studentportal.service.AuthService;
import com.auca.studentportal.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceApiClient financeApiClient;

    @MockBean
    private JwtUtil jwtUtil;

    // Mock AuthService so it doesn't try to connect to AUCA on startup
    @MockBean
    private AuthService authService;

    @Test
    void getMyBalance_withValidCookie_returnsBalance() throws Exception {
        BalanceResponse balance = new BalanceResponse();
        balance.setStudentId("25864");
        balance.setBalance(new BigDecimal("150000"));

        // Mock JwtUtil to extract studentId "25864" from any token
        when(jwtUtil.extractTokenFromCookie(anyString())).thenAnswer(inv -> {
            String cookie = inv.getArgument(0);
            // Extract token after "access_token="
            if (cookie != null && cookie.contains("access_token=")) {
                return cookie.split("access_token=")[1].split(";")[0];
            }
            return null;
        });
        when(jwtUtil.extractStudentId("test-token-value")).thenReturn("25864");

        // Mock Finance API call with studentId
        when(financeApiClient.getMyBalance(eq("25864"))).thenReturn(balance);

        mockMvc.perform(get("/api/v1/student/balance")
                        .header("Cookie", "access_token=test-token-value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value("25864"))
                .andExpect(jsonPath("$.data.balance").value(150000));
    }

    @Test
    void getMyPayments_withValidCookie_returnsPagedList() throws Exception {
        StudentPaymentResponse payment = new StudentPaymentResponse();
        payment.setId("PAY-001");
        payment.setAmount(new BigDecimal("300000"));
        payment.setStatus("SUCCESS");
        payment.setFeeType("TUITION_FEE");

        PagedResponse<StudentPaymentResponse> paged = new PagedResponse<>();
        paged.setData(List.of(payment));
        paged.setPage(1);
        paged.setPageSize(10);
        paged.setTotalPages(1);
        paged.setTotalElements(1);

        // Mock JWT extraction
        when(jwtUtil.extractTokenFromCookie(anyString())).thenAnswer(inv -> {
            String cookie = inv.getArgument(0);
            if (cookie != null && cookie.contains("access_token=")) {
                return cookie.split("access_token=")[1].split(";")[0];
            }
            return null;
        });
        when(jwtUtil.extractStudentId("test-token-value")).thenReturn("25864");

        when(financeApiClient.getMyPayments(eq("25864"), anyInt(), anyInt(), anyString()))
                .thenReturn(paged);

        mockMvc.perform(get("/api/v1/student/payments")
                        .header("Cookie", "access_token=test-token-value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.data[0].feeType").value("TUITION_FEE"));
    }

    @Test
    void getMyBalance_withoutCookie_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/student/balance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyBalance_withInvalidCookie_returnsUnauthorized() throws Exception {
        // Mock JWT extraction to fail
        when(jwtUtil.extractTokenFromCookie(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/v1/student/balance")
                        .header("Cookie", "access_token=invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
