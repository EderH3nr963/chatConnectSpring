package com.example.chatConnectSpring.shared.security.websocket;

import com.example.chatConnectSpring.chat.application.exceptions.MessageAccessDeniedException;
import com.example.chatConnectSpring.shared.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

@Component
public class WebSocketPrincipalResolver {

    public UUID resolveUserId(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth &&
                auth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        if (principal != null) {
            try {
                return UUID.fromString(principal.getName());
            } catch (IllegalArgumentException ignored) {
            }
        }

        throw new MessageAccessDeniedException("Usuário não autenticado no WebSocket.");
    }
}
