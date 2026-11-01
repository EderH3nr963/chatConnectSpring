package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.auth.infrastructure.security.websocket.WebSocketPrincipal;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatNotificationPort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.TypingEventDto;
import com.example.chatConnectSpring.auth.infrastructure.security.websocket.WebSocketPrincipalResolver;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatTypingWebSocketController {
    
    private final ChatNotificationPort chatNotificationPort;
    private final WebSocketPrincipalResolver webSocketPrincipalResolver;
    
    public ChatTypingWebSocketController(ChatNotificationPort chatNotificationPort, WebSocketPrincipalResolver webSocketPrincipalResolver) {
        this.chatNotificationPort = chatNotificationPort;
        this.webSocketPrincipalResolver = webSocketPrincipalResolver;
    }
    
    @MessageMapping("/chat.typing")
    public void handleTypingEvent(@Payload TypingEventDto payload, Principal principal) {
        TypingEventDto verifiedEvent = new TypingEventDto(
                payload.chatId(),
                webSocketPrincipalResolver.resolveUserId(principal),
                principal instanceof WebSocketPrincipal ? ((WebSocketPrincipal) principal).username() : null,
                payload.isTyping()
        );
        
        chatNotificationPort.notifyTypingStatus(verifiedEvent);
    }
}