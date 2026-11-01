package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "chat_participant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_chat_participant_user_chat", columnNames = {"chat_id", "user_id"}),
        },
        indexes = {
                @Index(name = "idx_chat_participant_chat_id", columnList = "chat_id"),
                @Index(name = "idx_chat_participant_user_id", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "chat_id", nullable = false)
    private UUID chatId;
    
    @Column(name = "unread_messages")
    private int unreadMessages = 0;
    
    @Column(name = "is_muted")
    private boolean isMuted = false;
    
    @Column(name = "last_read_message")
    private OffsetDateTime lastReadMessage = null;
    
    private OffsetDateTime joinedAt;
    
    @Enumerated(EnumType.STRING)
    private ChatParticipantRole role;
    
    @Column(name = "fixed_chat")
    private boolean fixedChat = false;
    
    @PrePersist
    public void prePersist() {
        if (joinedAt == null) {
            joinedAt = OffsetDateTime.now();
        }
    }
}
