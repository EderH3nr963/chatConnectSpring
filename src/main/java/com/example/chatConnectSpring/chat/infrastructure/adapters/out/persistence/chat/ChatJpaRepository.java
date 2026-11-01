package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.projections.ChatSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatJpaRepository extends JpaRepository<ChatEntity, UUID> {

    @Query(value = """
        SELECT
            c.id AS chatId,
            COALESCE(
                c.name,
                (
                    SELECT u.username
                    FROM chat_participant p_other
                    JOIN "user" u ON u.id = p_other.user_id
                    WHERE p_other.chat_id = c.id
                      AND p_other.user_id <> :userId
                    LIMIT 1
                )
            ) AS name,
            c.chat_type AS chatType,
            CASE
                WHEN c.chat_type = 'PRIVATE' THEN (
                    SELECT u.avatar_key
                    FROM chat_participant p_other
                    JOIN "user" u ON u.id = p_other.user_id
                    WHERE p_other.chat_id = c.id
                      AND p_other.user_id <> :userId
                    LIMIT 1
                )
                ELSE c.avatar_key
            END AS avatarKey,
            p.fixed_chat AS fixedChat,
            COALESCE(last_msg.content, '') AS lastMessageContent,
            last_msg.created_at AS lastMessageSentAt,
            p.unread_messages AS unreadCount,
            c.blocked AS blocked,
            c.blocked_at AS blockedAt
        FROM chat_participant p
        JOIN chat c ON c.id = p.chat_id
        LEFT JOIN LATERAL (
            SELECT m.content, m.created_at
            FROM message m
            WHERE m.chat_id = c.id
            ORDER BY m.created_at DESC
            LIMIT 1
        ) last_msg ON TRUE
        WHERE p.user_id = :userId
        ORDER BY p.fixed_chat DESC, last_msg.created_at DESC NULLS LAST
    """, nativeQuery = true)
    List<ChatSummaryProjection> findChatSummariesByUserId(@Param("userId") UUID userId);

    @Query(value = """
        SELECT CASE WHEN (
            EXISTS (
                SELECT 1 FROM friendship f
                WHERE f.status = 'ACCEPTED'
                  AND ((f.user_id = :senderId AND f.another_user_id = :targetId)
                    OR (f.user_id = :targetId AND f.another_user_id = :senderId))
            )
            AND NOT EXISTS (
                SELECT 1 FROM userblock b
                WHERE (b.blocker_id = :targetId AND b.blocked_id = :senderId)
                   OR (b.blocker_id = :senderId AND b.blocked_id = :targetId)
            )
        ) THEN true ELSE false END
    """, nativeQuery = true)
    boolean canSendMessageInPrivateChat(@Param("senderId") UUID senderId, @Param("targetId") UUID targetId);

    @Query(value = """
        SELECT c.*
        FROM chat c
        JOIN chat_participant p1 ON p1.chat_id = c.id
        JOIN chat_participant p2 ON p2.chat_id = c.id
        WHERE c.chat_type = 'PRIVATE'
          AND p1.user_id = :senderId
          AND p2.user_id = :targetId
        LIMIT 1
    """, nativeQuery = true)
    Optional<ChatEntity> findPrivateChat(@Param("senderId") UUID senderId, @Param("targetId") UUID targetId);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "UPDATE chat SET blocked = true, blocked_at = NOW() WHERE id = :chatId", nativeQuery = true)
    void markBlocked(@Param("chatId") UUID chatId);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "UPDATE chat SET blocked = false, blocked_at = NULL WHERE id = :chatId", nativeQuery = true)
    void clearBlocked(@Param("chatId") UUID chatId);

}
