package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.command.CreateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.port.in.chat.CreateChatUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class CreateChatUseCaseImpl implements CreateChatUseCase {
    private final ChatRepository chatRepository;
    
    public CreateChatUseCaseImpl(
            ChatRepository chatRepository
    ) {
        this.chatRepository = chatRepository;
    }
    
    @Override
    @Transactional
    public Chat create(CreateChatCommand command) {
        if (command.title() == null || command.title().isBlank()) {
            throw new IllegalArgumentException("Titulo é necessário");
        }
        
        Chat chat = new Chat();
        chat.setCreatedByUserId(command.createdByUserId());
        chat.setTitle(command.title().trim());
        chat.setType(command.type() == null ? ChatTypeEnum.CHAT : command.type());
        chat.setActive(true);
        
        return chatRepository.create(chat);
    }
}
