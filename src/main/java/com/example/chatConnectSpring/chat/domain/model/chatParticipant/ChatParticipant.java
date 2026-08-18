package com.example.chatConnectSpring.chat.domain.model.chatParticipant;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatParticipant {
    UUID id;
    String name;
    UUID userId;
    UUID chatId;
    OffsetDateTime joinedAt;
    ChatParticipantRole role;

    public ChatParticipantRole getRole() {
        return role;
    }
    
    public void setRole(ChatParticipantRole role) {
        this.role = role;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getChatId() {
        return chatId;
    }
    
    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }
    
    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(OffsetDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
