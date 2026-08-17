package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.command.AddParticipantCommand;
import com.example.chatConnectSpring.chat.application.exception.ChatInvalidException;
import com.example.chatConnectSpring.chat.application.exception.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exception.ForbbidenChatException;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.port.in.chat.AddParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AddParticipantUseCaseImpl implements AddParticipantUseCase {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public AddParticipantUseCaseImpl(
            ChatRepository chatRepository,
            ChatParticipantRepository chatParticipantRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
    }

    @Override
    @Transactional
    public ChatParticipant addParticipant(AddParticipantCommand command) {
        Chat chat = chatRepository.findById(command.chatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat não encontrado");
        }
        if (!chat.isActive()) {
            throw new ChatInvalidException("Chat está desativado");
        }
        if (command.userId() == null || command.participantId() == null) {
            throw new ChatInvalidException("Usuário ID e participante ID são necessários");
        }

        ChatParticipant myParticipant = chatParticipantRepository.getParticipantByChatIdAndUserId(command.chatId(), command.userId());
        if (myParticipant == null) {
            throw new ForbbidenChatException("Usuário não participa do chat");
        }
        if (myParticipant.getParticipantRole() != ParticipantRole.ADMIN) {
            throw new ForbbidenChatException("Somente administradores podem adicionar participantes");
        }

        if (chatParticipantRepository.getParticipantByChatIdAndUserId(command.chatId(), command.participantId()) != null) {
            throw new ChatInvalidException("Usuário já é participante do chat");
        }

        ChatParticipant participant = new ChatParticipant();
        participant.setChatId(command.chatId());
        participant.setUserId(command.participantId());
        participant.setParticipantName("Participante");
        participant.setJoinedAt(OffsetDateTime.now());
        participant.setMuted(false);
        participant.setBlocked(false);

        return chatParticipantRepository.addParticipant(participant);
    }
}
