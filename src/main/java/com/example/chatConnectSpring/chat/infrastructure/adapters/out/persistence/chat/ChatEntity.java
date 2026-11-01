package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat")
@Getter
@Setter
@SQLDelete(sql = "UPDATE chat SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = true)
    private String name;
    
    @Column(length = 1000, nullable = true)
    private String description;
    
    @Column(name = "avatar_key", length = 255)
    private String avatarKey = null;
    
    @Enumerated(EnumType.STRING)
    private ChatTypeEnum chatType;
    
    private OffsetDateTime createdAt;
    
    private OffsetDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "blocked", nullable = false)
    private boolean blocked = false;

    @Column(name = "blocked_at")
    private OffsetDateTime blockedAt;
    
    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}