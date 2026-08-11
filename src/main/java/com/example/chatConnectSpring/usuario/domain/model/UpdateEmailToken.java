package com.example.chatConnectSpring.usuario.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateEmailToken {
    UUID id;
    String token;
    UUID userId;
    String newEmail;
    LocalDateTime expiresAt;
    boolean used;
    
    
    public UpdateEmailToken(UUID id, String token, UUID userId, String newEmail, LocalDateTime expiresAt) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.newEmail = newEmail;
        this.expiresAt = expiresAt;
    }
    
    public UpdateEmailToken() {}
    
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
}
