package com.example.chatConnectSpring.chat.domain.port.out;

import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.ChatWebSocketEventResponseDTO;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

public interface ChatWebSocketBroadcaster {
    void registerChatSession(UUID chatId, WebSocketSession session);
    void unregisterSession(WebSocketSession session);
    void broadcastToChat(UUID chatId, ChatWebSocketEventResponseDTO payload) throws IOException;
}
