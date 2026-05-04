package com.auca.studentportal.client;

import com.auca.studentportal.config.AucaApiProperties;
import com.auca.studentportal.dto.*;
import com.auca.studentportal.exception.AucaApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceApiClientImpl implements FinanceApiClient {

    private final RestTemplate restTemplate;
    private final AucaApiProperties props;

    private static final String MY_PAYMENTS_PATH   = "/api/v1/finance/student-payments/my-payments";
    private static final String MY_FEES_PATH       = "/api/v1/finance/student-payments/my-fees";
    private static final String MY_BALANCE_PATH    = "/api/v1/finance/student-payments/my-balance";
    private static final String NOTIFY_PATH        = "/api/v1/finance/student-payments/notifications";

    @Override
    public PagedResponse<StudentPaymentResponse> getMyPayments(String cookieHeader, int page, int size, String sort) {
        String url = pagedUrl(MY_PAYMENTS_PATH, page, size, sort);
        log.info("GET my-payments → {}", url);
        try {
            ResponseEntity<PagedResponse<StudentPaymentResponse>> res = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(cookieHeaders(cookieHeader)),
                    new ParameterizedTypeReference<>() {}
            );
            return res.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw toAucaException("fetching payments", ex);
        }
    }

    @Override
    public PagedResponse<Object> getMyFees(String cookieHeader, int page, int size, String sort) {
        String url = pagedUrl(MY_FEES_PATH, page, size, sort);
        log.info("GET my-fees → {}", url);
        try {
            ResponseEntity<PagedResponse<Object>> res = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(cookieHeaders(cookieHeader)),
                    new ParameterizedTypeReference<>() {}
            );
            return res.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw toAucaException("fetching fees", ex);
        }
    }

    @Override
    public BalanceResponse getMyBalance(String cookieHeader) {
        String url = props.getBaseUrl() + MY_BALANCE_PATH;
        log.info("GET my-balance → {}", url);
        try {
            ResponseEntity<BalanceResponse> res = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(cookieHeaders(cookieHeader)),
                    BalanceResponse.class
            );
            return res.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw toAucaException("fetching balance", ex);
        }
    }



    @Override
    public void forwardNotification(FinanceNotificationRequest request, String serviceCookieHeader) {
        String url = props.getBaseUrl() + NOTIFY_PATH;
        log.info("POST notification → {}", url);
        try {
            restTemplate.exchange(
                    url, HttpMethod.POST,
                    new HttpEntity<>(request, cookieHeaders(serviceCookieHeader)),
                    Void.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw toAucaException("forwarding notification", ex);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private HttpHeaders cookieHeaders(String cookieHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (cookieHeader != null && !cookieHeader.isBlank()) {
            headers.set(HttpHeaders.COOKIE, cookieHeader);
        }
        return headers;
    }

    private String pagedUrl(String path, int page, int size, String sort) {
        return UriComponentsBuilder.fromHttpUrl(props.getBaseUrl() + path)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sort)
                .toUriString();
    }

    private AucaApiException toAucaException(String action, Exception ex) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        if (ex instanceof HttpClientErrorException hce) {
            status = HttpStatus.valueOf(hce.getStatusCode().value());
        }
        return new AucaApiException("AUCA API error while " + action + ": " + ex.getMessage(), status, ex);
    }
}
