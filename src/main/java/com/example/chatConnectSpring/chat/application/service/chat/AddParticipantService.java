package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.command.AddParticipantCommand;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.port.in.chat.AddParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AddParticipantService implements AddParticipantUseCase {
    
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    
    public AddParticipantService(
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
        if (!chat.isActive()) {
            throw new IllegalStateException("Chat está desativado");
        }
        if (command.userId() == null) {
            throw new IllegalArgumentException("Usuário ID é necessário");
        }
        
        ChatParticipant participant = new ChatParticipant();
        participant.setChatId(command.chatId());
        participant.setUserId(command.userId());
        participant.setJoinedAt(OffsetDateTime.now());
        participant.setMuted(false);
        participant.setBlocked(false);
        
        return chatParticipantRepository.addParticipant(participant);
    }
}
