package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.listenner;

import com.example.chatConnectSpring.chat.domain.ports.in.ManagePresenceUseCase;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;
import java.util.UUID;

@Component
public class StompEventListener {
    
    private final ManagePresenceUseCase presenceUseCase;
    
    public StompEventListener(ManagePresenceUseCase presenceUseCase) {
        this.presenceUseCase = presenceUseCase;
    }
    
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headers.getDestination();
        String subscriptionId = headers.getSubscriptionId();
        Principal user = headers.getUser();
        
        if (user != null && destination != null && destination.startsWith("/topic/chat.")) {
            if (headers.getSessionAttributes() != null) {
                headers.getSessionAttributes().put("sub_" + subscriptionId, destination);
            }
            
            try {
                UUID chatId = UUID.fromString(destination.replace("/topic/chat.", "").split("/")[0]);
                UUID userId = UUID.fromString(user.getName());
                presenceUseCase.enterChat(userId, chatId);
            } catch (Exception e) {
            }
        }
    }
    
    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String subscriptionId = headers.getSubscriptionId();
        Principal user = headers.getUser();
        
        String destination = null;
        if (headers.getSessionAttributes() != null) {
            destination = (String) headers.getSessionAttributes().get("sub_" + subscriptionId);
        }
        
        if (user != null && destination != null && destination.startsWith("/topic/chat.")) {
            try {
                UUID chatId = UUID.fromString(destination.replace("/topic/chat.", "").split("/")[0]);
                UUID userId = UUID.fromString(user.getName());
                
                presenceUseCase.exitChat(chatId, userId);
                headers.getSessionAttributes().remove("sub_" + subscriptionId);
            } catch (Exception e) {
            }
        }
    }
}
