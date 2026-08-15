package com.example.chatConnectSpring.chat.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.chat.domain.port.out.ChatWebSocketBroadcaster;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.ChatWebSocketEventResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketBroadcasterAdapter implements ChatWebSocketBroadcaster {

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<UUID, Set<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> sessionToChat = new ConcurrentHashMap<>();

    public ChatWebSocketBroadcasterAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerChatSession(UUID chatId, WebSocketSession session) {
        chatSessions.computeIfAbsent(chatId, key -> ConcurrentHashMap.newKeySet()).add(session);
        sessionToChat.put(session.getId(), chatId);
    }

    @Override
    public void unregisterSession(WebSocketSession session) {
        UUID chatId = sessionToChat.remove(session.getId());
        if (chatId != null) {
            Set<WebSocketSession> sessions = chatSessions.get(chatId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    chatSessions.remove(chatId);
                }
            }
        }
    }

    @Override
    public void broadcastToChat(UUID chatId, ChatWebSocketEventResponseDTO payload) throws IOException {
        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        String json = objectMapper.writeValueAsString(payload);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(json));
            }
        }
    }
}
