package com.example.chatConnectSpring.auth.infrastructure.security;

import com.example.chatConnectSpring.auth.domain.model.GeneratedRefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenCookieService {

    private static final String REFRESH_COOKIE = "refreshToken";
    private static final String EXPIRES_AT_COOKIE = "refreshExpiresAt";

    private final boolean secure;
    private final String sameSite;
    private final String path;

    public RefreshTokenCookieService(
            @Value("${auth.cookie.secure:true}") boolean secure,
            @Value("${auth.cookie.same-site:Strict}") String sameSite,
            @Value("${auth.cookie.path:/api/auth}") String path) {
        this.secure = secure;
        this.sameSite = sameSite;
        this.path = path;
    }

    public void addRefreshCookies(HttpServletResponse response, GeneratedRefreshToken refreshToken) {
        long maxAge = Duration.between(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC), refreshToken.expiresAt()).getSeconds();
        if (maxAge < 0) maxAge = 0;

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, refreshToken.value())
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(maxAge)
                .build();

        ResponseCookie expiresAtCookie = ResponseCookie.from(EXPIRES_AT_COOKIE, refreshToken.expiresAt().toString())
                .httpOnly(false)
                .secure(secure)
                .sameSite(sameSite)
                .path(path)
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expiresAtCookie.toString());
    }

    public void clearCookies(HttpServletResponse response) {
        ResponseCookie clearRefresh = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true).secure(secure).sameSite(sameSite).path(path).maxAge(0).build();
        
        ResponseCookie clearExpires = ResponseCookie.from(EXPIRES_AT_COOKIE, "")
                .httpOnly(false).secure(secure).sameSite(sameSite).path(path).maxAge(0).build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearExpires.toString());
    }
}
