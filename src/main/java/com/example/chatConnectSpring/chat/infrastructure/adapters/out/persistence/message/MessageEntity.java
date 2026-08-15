package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Audited.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatEntity chat;
    
    @Column(nullable = false, name = "user_id")
    private UUID userId;
    
    @Column(nullable = false)
    private String content;
    
    @CreationTimestamp
    private OffsetDateTime createdAt;
    
    @UpdateTimestamp
    private OffsetDateTime updatedAt;
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public ChatEntity getChat() { return chat; }
    public void setChatId(ChatEntity chat) { this.chat = chat; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    
}
