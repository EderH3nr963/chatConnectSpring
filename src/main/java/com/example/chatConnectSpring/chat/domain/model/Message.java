package com.example.chatConnectSpring.chat.domain.model;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Message {
    private UUID id;
    private UUID chatId;
    private UUID senderId;
    private ChatParticipant sender;
    private String content;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Message() {
    }

    public Message(UUID id, UUID chatId, UUID senderId, String content, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    
    public ChatParticipant getSender() {
        return sender;
    }
    
    public void setSender(ChatParticipant sender) {
        this.sender = sender;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
