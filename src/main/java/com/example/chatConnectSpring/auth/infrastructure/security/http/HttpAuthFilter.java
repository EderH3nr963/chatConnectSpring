package com.example.chatConnectSpring.auth.infrastructure.security.http;

import com.example.chatConnectSpring.auth.domain.model.vo.AccessTokenString;
import com.example.chatConnectSpring.auth.domain.ports.out.TokenIssuer;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class HttpAuthFilter extends OncePerRequestFilter {

    private final TokenIssuer tokenIssuer;

    public HttpAuthFilter(TokenIssuer tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        AccessTokenString token = extractToken(request);
        if (shouldSkip(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticate(token, request);
        } catch (Exception exception) {
            handleUnauthorized(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean shouldSkip(AccessTokenString token) {
        return token == null || SecurityContextHolder.getContext().getAuthentication() != null;
    }

    private void authenticate(AccessTokenString token, HttpServletRequest request) {
        ensureValidAccessToken(token);
        String userId = tokenIssuer.extractSubject(token);
        ensureSubjectPresent(userId);
        setAuthentication(UUID.fromString(userId), request);
    }

    private void ensureValidAccessToken(AccessTokenString token) {
        if (!tokenIssuer.isAccessToken(token)) {
            throw new BadCredentialsException("Invalid access token");
        }
    }

    private void ensureSubjectPresent(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new BadCredentialsException("Invalid access token");
        }
    }

    private void setAuthentication(UUID userId, HttpServletRequest request) {
        UserDetailsImpl userDetails = new UserDetailsImpl(userId);
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleUnauthorized(HttpServletResponse response) throws IOException {
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");
    }

    private AccessTokenString extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return AccessTokenString.of(authHeader.substring(7));
    }
}
