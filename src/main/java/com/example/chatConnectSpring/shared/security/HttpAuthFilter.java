package com.example.chatConnectSpring.shared.security;

import com.clerk.backend_api.helpers.security.AuthenticateRequest;
import com.clerk.backend_api.helpers.security.models.AuthenticateRequestOptions;
import com.clerk.backend_api.helpers.security.models.RequestState;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserIdentityProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HttpAuthFilter extends OncePerRequestFilter {
    
    @Value("${CLERK_SECRET_KEY}")
    private String clerkSecret;
    
    private final UserIdentityProvider userIdentityProvider;
    private final HandlerExceptionResolver resolver;
    
    public HttpAuthFilter(
            UserIdentityProvider userIdentityProvider,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.userIdentityProvider = userIdentityProvider;
        this.resolver = resolver;
    }
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        String authorization = request.getHeader("Authorization");
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            Map<String, List<String>> headers = new HashMap<>();
            
            request.getHeaderNames()
                    .asIterator()
                    .forEachRemaining(headerName -> {
                        List<String> values = new ArrayList<>();
                        request.getHeaders(headerName)
                                .asIterator()
                                .forEachRemaining(values::add);
                        headers.put(headerName, values);
                    });
            
            RequestState requestState =
                    AuthenticateRequest.authenticateRequest(
                            headers,
                            AuthenticateRequestOptions
                                    .secretKey(clerkSecret)
                                    .build()
                    );
            
            if (!requestState.isSignedIn()) {
                throw new BadCredentialsException("Invalid Clerk token");
            }
            
            String clerkId = (String) requestState
                    .claims()
                    .orElseThrow(() -> new BadCredentialsException("Clerk claims not found"))
                    .get("user_id");
            
            if (clerkId == null || clerkId.isBlank()) {
                throw new BadCredentialsException("Clerk user ID not found in token");
            }
            
            User user = userIdentityProvider.findByClerkUserId(clerkId);
            
            if (user == null) {
                throw new UsernameNotFoundException("User not found for Clerk ID: " + clerkId);
            }
            
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            
            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
            
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            filterChain.doFilter(request, response);
            
        } catch (Exception ex) {
            resolver.resolveException(request, response, null, ex);
        }
    }
}