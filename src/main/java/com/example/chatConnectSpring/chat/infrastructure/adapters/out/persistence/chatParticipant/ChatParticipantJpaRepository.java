package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantJpaRepository extends JpaRepository<ChatParticipantEntity, UUID> {
    Optional<ChatParticipantEntity> findByChatIdAndUserId(UUID chatId, UUID userId);
}
