package com.example.chatConnectSpring.presence.application.listeners;

import com.example.chatConnectSpring.auth.application.events.UserWebSocketConnectedEvent;
import com.example.chatConnectSpring.presence.domain.ports.in.PresenceUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WebSocketPresenceEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketPresenceEventListener.class);

    private final PresenceUseCase presenceUseCase;

    public WebSocketPresenceEventListener(PresenceUseCase presenceUseCase) {
        this.presenceUseCase = presenceUseCase;
    }

    @EventListener
    public void onUserConnected(UserWebSocketConnectedEvent event) {
        try {
            presenceUseCase.markOnline(event.userId());
        } catch (Exception ex) {
            log.warn("Falha ao marcar online userId={}", event.userId(), ex);
        }
    }
}
