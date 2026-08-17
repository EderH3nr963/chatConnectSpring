package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantJpaRepository extends JpaRepository<ChatParticipantEntity, UUID> {
    Optional<ChatParticipantEntity> findByChatIdAndUserId(UUID chatId, UUID userId);

    @Query("""
        SELECT COUNT(cp)
        FROM ChatParticipantEntity cp
        WHERE cp.chat.id = :chatId
          AND cp.participantRole = com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole.ADMIN
    """)
    long countAdminsByChatId(UUID chatId);

    @Query("""
        SELECT cp
        FROM ChatParticipantEntity cp
        WHERE cp.chat.id = :chatId
          AND cp.participantRole <> com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole.ADMIN
        ORDER BY cp.joinedAt ASC
    """)
    Optional<ChatParticipantEntity> findOldestNonAdminParticipant(UUID chatId);
}
