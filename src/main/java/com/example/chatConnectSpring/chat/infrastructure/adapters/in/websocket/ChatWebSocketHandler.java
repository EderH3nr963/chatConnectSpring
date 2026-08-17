package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ChatSessionRegistry registry;
    private final ObjectMapper objectMapper;

    public ChatWebSocketHandler(ChatSessionRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        UUID userId = registry.getUserId(session);
        if (userId != null) {
            registry.registerUserSession(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ChatWsMessage payload = objectMapper.readValue(message.getPayload(), ChatWsMessage.class);

        if (payload.type() == ChatWsMessageType.REGISTER && payload.userId() != null) {
            registry.registerUserSession(payload.userId(), session);
            return;
        }

        if (payload.type() == ChatWsMessageType.JOIN_CHAT && payload.chatId() != null) {
            registry.joinChat(payload.chatId(), session);
            return;
        }

        if (payload.type() == ChatWsMessageType.LEAVE_CHAT) {
            registry.leaveChat(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        registry.unregister(session);
    }

    public void broadcastChatCreated(Chat chat) {
        ChatWsMessage payload = new ChatWsMessage(
                ChatWsMessageType.CHAT_CREATED,
                chat.getCreatedByUserId(),
                chat.getId(),
                "Novo chat criado"
        );

        for (WebSocketSession session : registry.getAllUserSessions()) {
            sendSafely(session, payload);
        }
    }

    private void sendSafely(WebSocketSession session, ChatWsMessage payload) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
            }
        } catch (Exception ignored) {
        }
    }
}
