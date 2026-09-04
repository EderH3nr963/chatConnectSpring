package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatJpaRepository extends JpaRepository<ChatEntity, UUID> {
    @Query("""
        SELECT c
        FROM ChatEntity c
        JOIN ChatParticipantEntity p ON p.chatId = c.id
        WHERE p.userId = :userId
    """)
    List<ChatEntity> findChatsByUserId(@Param("userId") UUID userId);
}
