package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("""
        UPDATE ChatEntity c
        SET c.title = :title,
            c.description = :description,
            c.chatType = :chatType,
            c.updatedAt = current_timestamp
        WHERE c.id = :id
    """)
    void updateChat(
            @Param("id") UUID id,
            @Param("title") String title,
            @Param("description") String description,
            @Param("chatType") ChatTypeEnum chatType
    );
}
