package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.exception.ChatInvalidException;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.port.in.chat.FindChatsByUserIdUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FindChatsByUserIdUseCaseImpl implements FindChatsByUserIdUseCase {
    private final ChatRepository chatRepository;

    public FindChatsByUserIdUseCaseImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Chat> findChatsByUserId(UUID userId, Pageable pageable) {
        if (userId == null) {
            throw new ChatInvalidException("Usuário ID é necessário");
        }
        return chatRepository.findByUserId(userId, pageable);
    }
}
