package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionRegistry {
    private final ConcurrentHashMap<UUID, Set<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UUID> sessionChats = new ConcurrentHashMap<>();

    public void registerUserSession(UUID userId, WebSocketSession session) {
        userSessions.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unregister(WebSocketSession session) {
        UUID userId = getUserId(session);
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }

        UUID chatId = sessionChats.remove(session.getId());
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

    public void joinChat(UUID chatId, WebSocketSession session) {
        chatSessions.computeIfAbsent(chatId, key -> ConcurrentHashMap.newKeySet()).add(session);
        sessionChats.put(session.getId(), chatId);
    }

    public void leaveChat(WebSocketSession session) {
        UUID chatId = sessionChats.remove(session.getId());
        if (chatId == null) {
            return;
        }

        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                chatSessions.remove(chatId);
            }
        }
    }

    public Set<WebSocketSession> getChatSessions(UUID chatId) {
        return chatSessions.getOrDefault(chatId, Collections.emptySet());
    }

    public Set<WebSocketSession> getUserSessions(UUID userId) {
        return userSessions.getOrDefault(userId, Collections.emptySet());
    }

    public Set<WebSocketSession> getAllUserSessions() {
        return userSessions.values().stream()
                .flatMap(Set::stream)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    public UUID getUserId(WebSocketSession session) {
        Object value = session.getAttributes().get("userId");
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
