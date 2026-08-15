package com.example.chatConnectSpring.chat.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ChatMessage {
    private UUID id;
    private UUID chatId;
    private UUID userId;
    private String content;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean deleted;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getChatId() { return chatId; }
    public void setChatId(UUID chatId) { this.chatId = chatId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
