package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import com.example.chatConnectSpring.chat.domain.model.ChatMessage;
import com.example.chatConnectSpring.chat.domain.port.out.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessageRepositoryAdapter implements MessageRepository {

    private final MessageJpaRepository messageJpaRepository;
    
    public  MessageRepositoryAdapter(MessageJpaRepository messageJpaRepository) {
        this.messageJpaRepository = messageJpaRepository;
    }
    
    @Override
    public ChatMessage save(ChatMessage message) {
        return null;
    }

    @Override
    public Optional<ChatMessage> findById(UUID messageId) {
        return null;
    }
}
