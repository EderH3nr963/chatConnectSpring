package com.example.chatConnectSpring.chat.domain.model.chat;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Chat {
    private UUID id;
    private String title;
    private String description;
    private ChatTypeEnum chatType;
    private List<ChatParticipant> participants;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
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
