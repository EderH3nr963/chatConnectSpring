package com.example.chatConnectSpring.friendship.domain.model.userblock;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UserBlock {
    private UUID id;
    private UUID blockerId;
    private UUID blockedId;
    private String blockedUsername;
    private OffsetDateTime createdAt;
    
    public UserBlock() {
    }
    
    public UserBlock(UUID id, UUID blockerId, UUID blockedId, String blockedUsername, OffsetDateTime createdAt) {
        this.id = id;
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.blockedUsername = blockedUsername;
        this.createdAt = createdAt;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getBlockerId() {
        return blockerId;
    }
    
    public void setBlockerId(UUID blockerId) {
        this.blockerId = blockerId;
    }
    
    public UUID getBlockedId() {
        return blockedId;
    }
    
    public void setBlockedId(UUID blockedId) {
        this.blockedId = blockedId;
    }
    
    public String getBlockedUsername() {
        return blockedUsername;
    }
    
    public void setBlockedUsername(String blockedUsername) {
        this.blockedUsername = blockedUsername;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
