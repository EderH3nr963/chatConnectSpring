package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface MessageJpaRepository extends JpaRepository<MessageEntity, UUID> {
    
    @Query("""
        SELECT m
        FROM MessageEntity m
        JOIN FETCH m.chat
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.reply r
        LEFT JOIN FETCH r.sender
        LEFT JOIN FETCH r.chat
        WHERE m.id = :id
    """)
    Optional<MessageEntity> findById(@Param("id") UUID id);

    @Query("""
        SELECT m
        FROM MessageEntity m
        JOIN FETCH m.chat
        JOIN FETCH m.sender
        LEFT JOIN FETCH m.reply r
        LEFT JOIN FETCH r.sender
        WHERE m.chatId = :chatId
    """)
    Slice<MessageEntity> findByChatId(@Param("chatId") UUID chatId, Pageable pageable);


    @Query("""
        SELECT m
        FROM MessageEntity m
        JOIN FETCH m.chat
        JOIN FETCH m.sender
        WHERE m.chatId = :chatId
        ORDER BY m.createdAt DESC
        LIMIT 1
    """)
    Optional<MessageEntity> findFirstByChatIdOrderByCreatedAtDesc(@Param("chatId") UUID chatId);

    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE m.chatId = :chatId")
    void deleteByChatId(@Param("chatId") UUID chatId);

    @Query("""
        SELECT m FROM MessageEntity m
        WHERE m.chatId = :chatId AND m.fileS3Key IS NOT NULL AND m.fileS3Key <> ''
    """)
    java.util.List<MessageEntity> findWithAttachmentByChatId(@Param("chatId") UUID chatId);
    
    @Modifying
    @Query(value = """
        DELETE FROM message
        WHERE status = 'PENDING'::message_status\s
          AND created_at < CURRENT_TIMESTAMP - INTERVAL '15 minutes';
    """, nativeQuery = true)
    void deleteOldPendingMessages();
}
