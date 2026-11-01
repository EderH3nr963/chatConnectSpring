package com.example.chatConnectSpring.chat.domain.model.chatParticipant;

import com.example.chatConnectSpring.user.domain.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatParticipant {
    UUID id;
    String username;
    UUID userId;
    User user;
    UUID chatId;
    OffsetDateTime lastReadMessage;
    int unreadMessages;
    OffsetDateTime joinedAt;
    ChatParticipantRole role;
    boolean muted;
    
    public String getUsername() {
        return username;
    }
    
    public String getUserDisplayName() {
        if (user != null && user.getUsername() != null) {
            return user.getUsername();
        }
        return username;
    }
    
    public String getUserAvatarKey() {
        if (user != null) {
            return user.getAvatarKey();
        }
        return null;
    }
    
    public OffsetDateTime getLastReadMessage() {
        return lastReadMessage;
    }
    
    public void setLastReadMessage(OffsetDateTime lastReadMessage) {
        this.lastReadMessage = lastReadMessage;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public UUID getChatId() {
        return chatId;
    }
    
    public int getUnreadMessages() {
        return unreadMessages;
    }
    
    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
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
    
    public boolean isMuted() {
        return muted;
    }
    
    public void setMuted(boolean muted) {
        this.muted = muted;
    }
}
