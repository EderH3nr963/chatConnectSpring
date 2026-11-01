package com.example.chatConnectSpring.user.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String email;
    private String username;
    private String avatarKey;
    private boolean active2fa;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public User() {
    }

    public User(UUID id, String email, String username, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this(id, email, username, null, false, createdAt, updatedAt);
    }
    
    public User(UUID id, String email, String username, String avatarKey, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
     this(id, email, username, avatarKey, false, createdAt, updatedAt);
    }

    public User(UUID id, String email, String username, String avatarKey, boolean active2fa, OffsetDateTime createdAt, OffsetDateTime updatedAt) {        this.id = id;
        this.email = email;
        this.username = username;
        this.avatarKey = avatarKey;
        this.active2fa = active2fa;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    
    public boolean isActive2fa() {
        return active2fa;
    }
    
    public void setActive2fa(boolean active2fa) {
        this.active2fa = active2fa;
    }
    
    public boolean requiresTwoFactorVerification() {
        return active2fa;
    }
    
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAvatarKey() {
        return avatarKey;
    }

    public void setAvatarKey(String avatarKey) {
        this.avatarKey = avatarKey;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
