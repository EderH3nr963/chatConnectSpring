package com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.updateEmailToken;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_change_tokens")
public class UpdateEmailTokenEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(name = "new_email", nullable = false)
    private String newEmail;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public UpdateEmailTokenEntity() {
    }
    
    public UpdateEmailTokenEntity(
            String token,
            String newEmail,
            UUID userId,
            LocalDateTime expiresAt
    ) {
        this.token = token;
        this.newEmail = newEmail;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }
    
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {}
    
    public String getToken() {
        return token;
    }
    public void setToken(String token) {}
    
    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {}
    
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {}
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {}
    
    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {}
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {}
    
    public void markAsUsed() {
        this.used = true;
    }
}