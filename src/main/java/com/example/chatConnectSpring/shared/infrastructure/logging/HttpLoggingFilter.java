package com.example.chatConnectSpring.shared.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class HttpLoggingFilter extends OncePerRequestFilter {
    private static final int CACHE_LIMIT = 1024 * 1024;

    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        boolean isHeartbeat = uri.contains("/heartbeat") || uri.contains("/presence");

        long start = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedReq = new ContentCachingRequestWrapper(request, CACHE_LIMIT);
        ContentCachingResponseWrapper wrappedRes = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(wrappedReq, wrappedRes);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = wrappedRes.getStatus();

            if (isHeartbeat) {
                log.debug("{} {} -> {} ({}ms) traceId={}", request.getMethod(), uri, status, duration, MDC.get(TraceIdFilter.TRACE_ID_MDC));
            } else if (status >= 500) {
                log.error("{} {} -> {} ({}ms) traceId={}", request.getMethod(), uri, status, duration, MDC.get(TraceIdFilter.TRACE_ID_MDC));
            } else if (status >= 400) {
                log.warn("{} {} -> {} ({}ms) traceId={}", request.getMethod(), uri, status, duration, MDC.get(TraceIdFilter.TRACE_ID_MDC));
            } else {
                log.info("{} {} -> {} ({}ms)", request.getMethod(), uri, status, duration);
            }
            
            wrappedRes.copyBodyToResponse();
        }
    }
}
