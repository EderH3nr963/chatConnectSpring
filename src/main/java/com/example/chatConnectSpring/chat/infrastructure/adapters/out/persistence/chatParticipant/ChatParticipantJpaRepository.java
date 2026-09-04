package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatParticipantJpaRepository extends JpaRepository<ChatParticipantEntity, UUID> {
    java.util.Optional<ChatParticipantEntity> findByUserIdAndChatId(UUID userId, UUID chatId);

    List<ChatParticipantEntity> findByChatId(UUID chatId);

    @Modifying
    void deleteByUserIdAndChatId(UUID userId, UUID chatId);
    
    @Modifying
    @Query("""
        DELETE FROM ChatParticipantEntity c
        WHERE c.chatId = :chatId
    """)
    void deleteAllByChatId(@Param("chatId") UUID chatId);
}
