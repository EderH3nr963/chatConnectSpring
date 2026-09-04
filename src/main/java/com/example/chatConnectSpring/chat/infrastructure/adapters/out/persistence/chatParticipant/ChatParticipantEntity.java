package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
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
    
    @Column(name = "chat_id", nullable = false)
    private UUID chatId;
    
    @Column(name = "unread_messages")
    private int unreadMessages = 0;
    
    private OffsetDateTime joinedAt;
    
    @Enumerated(EnumType.STRING)
    private ChatParticipantRole role;
    
    @PrePersist
    public void prePersist() {
        if (joinedAt == null) {
            joinedAt = OffsetDateTime.now();
        }
    }
}
