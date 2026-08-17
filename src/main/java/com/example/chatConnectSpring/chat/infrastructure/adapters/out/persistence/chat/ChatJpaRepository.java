package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ChatJpaRepository extends JpaRepository<ChatEntity, UUID> {
    @Query("""
        SELECT c
        FROM ChatEntity c
        JOIN c.participants p
        WHERE p.userId = :userId
    """)
    Page<ChatEntity> findByUserId(UUID userId, Pageable pageable);
}
