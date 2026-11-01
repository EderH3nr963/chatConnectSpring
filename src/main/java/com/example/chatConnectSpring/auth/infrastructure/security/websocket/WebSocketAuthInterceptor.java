package com.example.chatConnectSpring.auth.infrastructure.security.websocket;

import com.example.chatConnectSpring.auth.application.events.UserWebSocketConnectedEvent;
import com.example.chatConnectSpring.auth.domain.model.vo.AccessTokenString;
import com.example.chatConnectSpring.auth.domain.ports.out.TokenIssuer;
import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import org.springframework.context.ApplicationEventPublisher;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final UserUseCase userUseCase;
    private final TokenIssuer tokenIssuer;
    private final ApplicationEventPublisher eventPublisher;

    public WebSocketAuthInterceptor(UserUseCase userUseCase, TokenIssuer tokenIssuer, ApplicationEventPublisher eventPublisher) {
        this.userUseCase = userUseCase;
        this.tokenIssuer = tokenIssuer;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public @Nullable Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (shouldSkip(accessor)) {
            return message;
        }
        authenticate(accessor);
        return message;
    }

    private boolean shouldSkip(StompHeaderAccessor accessor) {
        return accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand());
    }

    private void authenticate(StompHeaderAccessor accessor) {
        AccessTokenString token = extractToken(accessor);
        ensureTokenPresent(token);
        ensureValidAccessToken(token);
        try {
            String userId = tokenIssuer.extractSubject(token);
            ensureSubjectPresent(userId);
            User user = findUserOrFail(userId);
            setPrincipal(accessor, user);
            eventPublisher.publishEvent(new UserWebSocketConnectedEvent(UUID.fromString(userId)));
        } catch (MessagingException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new MessagingException("Invalid access token", exception);
        }
    }

    private void ensureTokenPresent(AccessTokenString token) {
        if (token == null) {
            throw new MessagingException("Invalid or missing access token");
        }
    }

    private void ensureValidAccessToken(AccessTokenString token) {
        if (!tokenIssuer.isAccessToken(token)) {
            throw new MessagingException("Invalid or missing access token");
        }
    }

    private void ensureSubjectPresent(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new MessagingException("Invalid access token");
        }
    }

    private User findUserOrFail(String userId) {
        User user = userUseCase.findById(UUID.fromString(userId));
        if (user == null) {
            throw new MessagingException("Invalid access token");
        }
        return user;
    }

    private void setPrincipal(StompHeaderAccessor accessor, User user) {
        Principal principal = new WebSocketPrincipal(user);
        accessor.setUser(principal);
    }

    private AccessTokenString extractToken(StompHeaderAccessor accessor) {
        List<String> authorization = accessor.getNativeHeader("Authorization");
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        String authHeader = authorization.get(0);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return AccessTokenString.of(authHeader.substring(7));
    }
}
