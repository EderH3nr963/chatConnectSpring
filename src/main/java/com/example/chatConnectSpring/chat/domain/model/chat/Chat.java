package com.example.chatConnectSpring.chat.domain.model.chat;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Chat {
    private UUID id;
    private String name;
    private String avatarKey;
    private String description;
    private ChatTypeEnum chatType;
    private List<ChatParticipant> participants;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean blocked = false;
    private OffsetDateTime blockedAt;

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public OffsetDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(OffsetDateTime blockedAt) { this.blockedAt = blockedAt; }
    
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
    
    public String getAvatarKey() {
        return avatarKey;
    }
    
    public void setAvatarKey(String avatarKey) {
        this.avatarKey = avatarKey;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ChatTypeEnum getChatType() {
        return chatType;
    }
    
    public void setChatType(ChatTypeEnum chatType) {
        this.chatType = chatType;
    }
    
    public List<ChatParticipant> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<ChatParticipant> participants) {
        this.participants = participants;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
