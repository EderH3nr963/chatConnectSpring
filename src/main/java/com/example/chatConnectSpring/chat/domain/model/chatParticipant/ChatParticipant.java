package com.example.chatConnectSpring.chat.domain.model.chatParticipant;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatParticipant {
    UUID id;
    String username;
    UUID userId;
    UUID chatId;
    int unreadMessages;
    OffsetDateTime joinedAt;
    ChatParticipantRole role;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
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
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public UUID getChatId() {
        return chatId;
    }
    
    public int getUnreadMessages() { return unreadMessages; }
    
    public void setUnreadMessages(int unreadMessages) { this.unreadMessages = unreadMessages; }
    
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
