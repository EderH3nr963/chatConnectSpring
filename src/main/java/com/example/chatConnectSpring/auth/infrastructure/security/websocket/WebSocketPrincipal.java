package com.example.chatConnectSpring.auth.infrastructure.security.websocket;

import com.example.chatConnectSpring.user.domain.model.User;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.UUID;

public class WebSocketPrincipal implements Principal {
    private final UUID userId;
    private final String username;
    
    public WebSocketPrincipal(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
    }
    
    @Override
    public String getName() {
        return userId.toString();
    }
    
    public String username() {
        return username;
    }
    
}
