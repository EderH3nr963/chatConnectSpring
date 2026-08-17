package com.example.chatConnectSpring.chat.domain.port.in.chat;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FindChatsByUserIdUseCase {
    Page<Chat> findChatsByUserId(UUID userId, Pageable pageable);
}
