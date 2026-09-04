package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findByChatIdOrderByCreatedAtAsc(UUID chatId);

    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE m.chatId = :chatId")
    void deleteByChatId(@Param("chatId") UUID chatId);
}
