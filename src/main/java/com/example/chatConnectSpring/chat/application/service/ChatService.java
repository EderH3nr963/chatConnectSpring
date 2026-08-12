package com.example.chatConnectSpring.chat.application.service;

import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.application.command.AddParticipantCommand;
import com.example.chatConnectSpring.chat.application.command.CreateChatCommand;
import com.example.chatConnectSpring.chat.domain.port.in.AddParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.CreateChatUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.FindChatByIdUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.port.out.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ChatService implements
        CreateChatUseCase,
        AddParticipantUseCase,
        FindChatByIdUseCase {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public ChatService(
            ChatRepository chatRepository,
            ChatParticipantRepository chatParticipantRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
    }

    @Override
    @Transactional
    public Chat create(UUID createdByUserId, String title, ChatTypeEnum type) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Titulo é necessário");
        }

        Chat chat = new Chat();
        chat.setCreatedByUserId(createdByUserId);
        chat.setTitle(title.trim());
        chat.setType(type == null ? ChatTypeEnum.CHAT : type);
        chat.setCreatedAt(OffsetDateTime.now());
        chat.setActive(true);

        return chatRepository.create(chat);
    }

    public Chat create(CreateChatCommand command) {
        return create(command.createdByUserId(), command.title(), command.type());
    }

    @Override
    @Transactional
    public ChatParticipant addParticipant(UUID chatId, UUID userId) {
        Chat chat = findById(chatId);
        if (!chat.isActive()) {
            throw new IllegalStateException("Chat está desativado");
        }
        if (userId == null) {
            throw new IllegalArgumentException("Usuário ID é necessário");
        }

        ChatParticipant participant = new ChatParticipant();
        participant.setChatId(chatId);
        participant.setUserId(userId);
        participant.setJoinedAt(OffsetDateTime.now());
        participant.setMuted(false);
        participant.setBlocked(false);

        return chatParticipantRepository.addParticipant(participant);
    }

    public ChatParticipant addParticipant(AddParticipantCommand command) {
        return addParticipant(command.chatId(), command.userId());
    }

    @Override
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
