package com.example.chatConnectSpring.presence.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.presence.domain.ports.in.PresenceUseCase;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.UUID;

@Component
public class PresenceStompListener {

    private final PresenceUseCase presenceUseCase;

    public PresenceStompListener(PresenceUseCase presenceUseCase) {
        this.presenceUseCase = presenceUseCase;
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        UUID userId = extractUserId(event);
        if (userId == null) return;
        presenceUseCase.markOffline(userId);
    }
    
    private UUID extractUserId(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        
        if (accessor.getUser() instanceof Authentication authentication && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        
        return null;
    }
}
