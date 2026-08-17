package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.shared.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = extractToken(request);
        if (token == null || !jwtService.tokenValido(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        UUID userId = UUID.fromString(jwtService.extractId(token));
        attributes.put("userId", userId);
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }

    private String extractToken(ServerHttpRequest request) {
        List<String> authorization = request.getHeaders().get("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            String header = authorization.get(0);
            if (header.startsWith("Bearer ")) {
                return header.substring(7);
            }
        }

        var params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        List<String> tokenValues = params.get("token");
        if (tokenValues != null && !tokenValues.isEmpty()) {
            return tokenValues.get(0);
        }
        return null;
    }
}
