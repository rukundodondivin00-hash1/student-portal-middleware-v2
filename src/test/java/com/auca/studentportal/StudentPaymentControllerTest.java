package com.auca.studentportal;

import com.auca.studentportal.client.FinanceApiClient;
import com.auca.studentportal.dto.*;
import com.auca.studentportal.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Base64;
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

    // Helper method to create a fake JWT token with specific student ID
    private String createFakeJwtToken(String studentId) {
        // This is a simplified fake JWT (not real signature)
        return "eyJhbGciOiJIUzI4NCJ9." + 
               Base64.getUrlEncoder().encodeToString(
                   ("{\"sub\":\"" + studentId + "\",\"username\":\"" + studentId + "\"}")
                       .getBytes()
               ) + 
               ".fakeSignature";
    }

    @Test
    void getMyBalance_withValidToken_returnsBalance() throws Exception {
        // Arrange
        String studentId = "25864";
        String fakeToken = createFakeJwtToken(studentId);
        
        BalanceResponse balance = new BalanceResponse();
        balance.setStudentId(studentId);
        balance.setBalance(new BigDecimal("150000"));

        // Mock JwtUtil to extract student ID from any token
        when(jwtUtil.extractTokenFromCookie(anyString())).thenAnswer(invocation -> {
            String cookie = invocation.getArgument(0);
            if (cookie != null && cookie.contains("access_token=")) {
                return cookie.substring(cookie.indexOf("access_token=") + 13)
                            .split(";")[0]
                            .trim();
            }
            return null;
        });
        when(jwtUtil.extractStudentId(fakeToken)).thenReturn(studentId);

        when(financeApiClient.getMyBalance(eq(studentId))).thenReturn(balance);

        // Act & Assert
        mockMvc.perform(get("/api/v1/student/balance")
                        .header("Cookie", "access_token=" + fakeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value(studentId))
                .andExpect(jsonPath("$.data.balance").value(150000));
    }

    @Test
    void getMyPayments_withValidToken_returnsPagedList() throws Exception {
        // Arrange
        String studentId = "25864";
        String fakeToken = createFakeJwtToken(studentId);
        
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

        when(jwtUtil.extractTokenFromCookie(anyString())).thenAnswer(invocation -> {
            String cookie = invocation.getArgument(0);
            if (cookie != null && cookie.contains("access_token=")) {
                return cookie.substring(cookie.indexOf("access_token=") + 13)
                            .split(";")[0]
                            .trim();
            }
            return null;
        });
        when(jwtUtil.extractStudentId(fakeToken)).thenReturn(studentId);

        when(financeApiClient.getMyPayments(eq(studentId), anyInt(), anyInt(), anyString()))
                .thenReturn(paged);

        // Act & Assert
        mockMvc.perform(get("/api/v1/student/payments")
                        .header("Cookie", "access_token=" + fakeToken))
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
    void getMyBalance_withInvalidToken_returnsUnauthorized() throws Exception {
        // Mock JWT extraction to fail (invalid token)
        when(jwtUtil.extractTokenFromCookie(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/v1/student/balance")
                        .header("Cookie", "access_token=invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyBalance_withMalformedToken_returnsUnauthorized() throws Exception {
        // Mock token extraction to succeed but student ID extraction to fail
        String fakeToken = "malformed.token.here";
        
        when(jwtUtil.extractTokenFromCookie(anyString())).thenReturn(fakeToken);
        when(jwtUtil.extractStudentId(fakeToken)).thenReturn(null);

        mockMvc.perform(get("/api/v1/student/balance")
                        .header("Cookie", "access_token=" + fakeToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMyFees_withValidToken_returnsPagedList() throws Exception {
        // Arrange
        String studentId = "25864";
        String fakeToken = createFakeJwtToken(studentId);
        
        PagedResponse<Object> paged = new PagedResponse<>();
        paged.setData(List.of(new Object()));
        paged.setPage(1);
        paged.setPageSize(10);
        paged.setTotalPages(1);
        paged.setTotalElements(1);

        when(jwtUtil.extractTokenFromCookie(anyString())).thenAnswer(invocation -> {
            String cookie = invocation.getArgument(0);
            if (cookie != null && cookie.contains("access_token=")) {
                return cookie.substring(cookie.indexOf("access_token=") + 13)
                            .split(";")[0]
                            .trim();
            }
            return null;
        });
        when(jwtUtil.extractStudentId(fakeToken)).thenReturn(studentId);

        when(financeApiClient.getMyFees(eq(studentId), anyInt(), anyInt(), anyString()))
                .thenReturn(paged);

        // Act & Assert
        mockMvc.perform(get("/api/v1/student/fees")
                        .header("Cookie", "access_token=" + fakeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray());
    }
}
