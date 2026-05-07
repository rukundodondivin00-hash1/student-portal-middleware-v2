package com.auca.studentportal.client;

import com.auca.studentportal.config.AucaApiProperties;
import com.auca.studentportal.cookie.CookieManager;
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
    private final CookieManager cookieManager;

    // Path templates with %s placeholder for studentId
    private static final String PAYMENTS_PATH_TEMPLATE = "/api/v1/finance/student-payments/%s/my-payments";
    private static final String FEES_PATH_TEMPLATE     = "/api/v1/finance/student-payments/%s/my-fees";
    private static final String BALANCE_PATH_TEMPLATE  = "/api/v1/finance/student-payments/%s/my-balance";
    private static final String NOTIFY_PATH            = "/api/v1/finance/student-payments/notifications";

    @Override
    public PagedResponse<StudentPaymentResponse> getMyPayments(String studentId, int page, int size, String sort) {
        String path = String.format(PAYMENTS_PATH_TEMPLATE, studentId);
        String url = pagedUrl(path, page, size, sort);
        log.info("GET my-payments for student {} → {}", studentId, url);
        try {
            ResponseEntity<PagedResponse<StudentPaymentResponse>> res = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(serviceCookieHeaders()),
                    new ParameterizedTypeReference<>() {}
            );
            return res.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw toAucaException("fetching payments", ex);
        }
    }

    @Override
    public PagedResponse<Object> getMyFees(String studentId, int page, int size, String sort) {
        String path = String.format(FEES_PATH_TEMPLATE, studentId);
        String url = pagedUrl(path, page, size, sort);
        log.info("GET my-fees for student {} → {}", studentId, url);
        try {
            ResponseEntity<PagedResponse<Object>> res = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(serviceCookieHeaders()),
                    new ParameterizedTypeReference<>() {}
            );
            return res.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw toAucaException("fetching fees", ex);
        }
    }

    @Override
    public BalanceResponse getMyBalance(String studentId) {
        String path = String.format(BALANCE_PATH_TEMPLATE, studentId);
        String url = props.getBaseUrl() + path;
        log.info("GET my-balance for student {} → {}", studentId, url);
        try {
            ResponseEntity<BalanceResponse> res = restTemplate.exchange(
                    url, HttpMethod.GET,
                    new HttpEntity<>(serviceCookieHeaders()),
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

    /**
     * Builds headers with the middleware service account's AUCA cookies.
     * Uses cookies stored in CookieManager after authentication.
     */
    private HttpHeaders serviceCookieHeaders() {
        String cookieHeader = cookieManager.getServiceCookieHeader();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            log.warn("Service account cookies not available — AUCA may not be authenticated");
            // Fall back to empty cookies; will likely fail
            return new HttpHeaders();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.COOKIE, cookieHeader);
        return headers;
    }

    /**
     * Cookie headers for legacy webhook calls (passed in as parameter).
     * @deprecated Use serviceCookieHeaders() for student data calls
     */
    @Deprecated
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
