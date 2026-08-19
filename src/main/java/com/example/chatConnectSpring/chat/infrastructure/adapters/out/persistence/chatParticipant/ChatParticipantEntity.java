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
@Table(name = "chat_participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID chatId;

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
