package com.example.chatConnectSpring.chat.domain.port.out.chat;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChatRepository {
    Chat findById(UUID chatId);
    
    Chat create(Chat chat);

    Chat save(Chat chat);

    void delete(UUID chatId);
    
    Page<Chat> findByUserId(UUID userId, Pageable pageable);
}
