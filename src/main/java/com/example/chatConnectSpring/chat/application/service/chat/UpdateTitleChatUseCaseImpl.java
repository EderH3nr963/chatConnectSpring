package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.command.UpdateTitleChatCommand;
import com.example.chatConnectSpring.chat.application.exception.ChatInvalidException;
import com.example.chatConnectSpring.chat.application.exception.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exception.ForbbidenChatException;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.port.in.chat.UpdateTitleChatUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateTitleChatUseCaseImpl implements UpdateTitleChatUseCase {
    private final ChatRepository chatRepository;

    public UpdateTitleChatUseCaseImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public Chat updateTitle(UpdateTitleChatCommand command) {
        if (command.chatId() == null) {
            throw new ChatInvalidException("Chat ID é necessário");
        }
        if (command.userId() == null) {
            throw new ChatInvalidException("Usuário ID é necessário");
        }
        if (command.newTitle() == null || command.newTitle().isBlank()) {
            throw new ChatInvalidException("Título é necessário");
        }

        Chat chat = chatRepository.findById(command.chatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat não encontrado");
        }
        if (!chat.isActive()) {
            throw new ChatInvalidException("Chat está desativado");
        }
        if (!command.userId().equals(chat.getCreatedByUserId())) {
            throw new ForbbidenChatException("Somente o criador pode alterar o título do chat");
        }

        chat.setTitle(command.newTitle().trim());
        return chatRepository.save(chat);
    }
}
