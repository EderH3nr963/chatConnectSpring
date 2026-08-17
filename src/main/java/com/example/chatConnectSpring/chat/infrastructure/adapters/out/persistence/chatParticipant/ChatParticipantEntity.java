package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;
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
                @UniqueConstraint(name = "uk_chat_participant_chat_user", columnNames = {"chat_id", "user_id"})
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatEntity chat;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name="participant_name", nullable = false)
    private String participantName;

    @Column(nullable = false)
    private OffsetDateTime joinedAt = OffsetDateTime.now();
    
    @Column(nullable = false)
    private ParticipantRole participantRole;

    private OffsetDateTime lastReadAt;

    @Column(nullable = false)
    private boolean muted = false;

    @Column(nullable = false)
    private boolean blocked = false;
}
