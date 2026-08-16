package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.port.in.chat.FindChatByIdUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FindChatByIdService implements FindChatByIdUseCase {
    private final ChatRepository chatRepository;
    
    public FindChatByIdService(
            ChatRepository chatRepository
    ) {
        this.chatRepository = chatRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Chat findById(UUID chatId) {
        if (chatId == null) {
            throw new IllegalArgumentException("Chat ID é necessário");
        }
        
        Chat chat = chatRepository.findById(chatId);
        if (chat == null) {
            throw new IllegalArgumentException("Chat não encontrado");
        }
        return chat;
    }
}
