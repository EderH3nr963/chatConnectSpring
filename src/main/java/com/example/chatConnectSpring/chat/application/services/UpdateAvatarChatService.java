package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.in.UpdateAvatarChatUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateAvatarChatService implements UpdateAvatarChatUseCase {

    private final ChatRepository chatRepository;

    UpdateAvatarChatService(final ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    @Override
    public void execute(UUID chatId, String objectKey) {
        Chat chat = chatRepository.findById(chatId);
        if (chat == null) {
            throw new ChatNotFoundException("Chat with id " + chatId + " not found");
        }

        chat.setAvatarKey(objectKey);
        chatRepository.save(chat);
    }
}
