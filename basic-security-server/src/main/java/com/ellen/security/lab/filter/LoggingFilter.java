package com.ellen.security.lab.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class LoggingFilter implements Filter {
    public static final String REQUEST_UUID_MDC_KEY = "request_uuid";
    public static final String TRACE_ID_MDC_KEY = "trace_id";
    private static final String REQUEST_UUID_RESPONSE_HEADER = "X-Request-UUID";
    private static final String UNAUTHENTICATED = "unauthenticated";
    private final KeyValueLogger logger;

    public LoggingFilter(KeyValueLogger logger) {
        this.logger = logger;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Instant start = Instant.now();

        HttpServletRequest httpServletRequest = getHttpServletRequest(servletRequest);
        String path = httpServletRequest.getRequestURI();
        String requestUuid = createRequestUuid();
        MDC.put(REQUEST_UUID_MDC_KEY, requestUuid);
        MDC.put(TRACE_ID_MDC_KEY, requestUuid);
        Optional<Authentication> authentication = getAuthentication();
        String authority = authentication.map(auth -> auth.getAuthorities().toString()).orElse(UNAUTHENTICATED);
        String userName = authentication.map(Principal::getName).orElse(UNAUTHENTICATED);

        logger.log(Logs.builder().build().put("phase", "request_start")
                .put("id", requestUuid)
                .put("authority", authority)
                .put("username", userName)
                .put("method", httpServletRequest.getMethod())
                .put("path", path)
                .map);

        filterChain.doFilter(servletRequest, servletResponse);
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        logger.log(Logs.builder().build().put("phase", "request_complete")
                .put("id", requestUuid)
                .put("username", userName)
                .put("method", httpServletRequest.getMethod())
                .put("path", path)
                .put("response_status", httpServletResponse.getStatus())
                .put("response_length_bytes", httpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH))
                .put("response_content_type", httpServletResponse.getContentType())
                .put("request_duration_ms", Duration.between(start, Instant.now()).toMillis())
                .map);

        httpServletResponse.setHeader(REQUEST_UUID_RESPONSE_HEADER, requestUuid);
        MDC.remove(REQUEST_UUID_MDC_KEY);
    }

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication());
    }

    private String createRequestUuid() {
        return UUID.randomUUID().toString();
    }

    private HttpServletRequest getHttpServletRequest(ServletRequest request) {
        HttpServletRequest httpServletRequest;
        if (request instanceof HttpServletRequest) {
            httpServletRequest = (HttpServletRequest) request;
        } else {
            httpServletRequest = ((ServletWebRequest) request).getRequest();
        }
        return httpServletRequest;
    }

    @Builder
    @Getter
    private static class Logs {
        private final Map<String, Object> map = new LinkedHashMap<>();

        Logs put(String key, Object value) {
            map.put(key, value);
            return this;
        }
    }
}
