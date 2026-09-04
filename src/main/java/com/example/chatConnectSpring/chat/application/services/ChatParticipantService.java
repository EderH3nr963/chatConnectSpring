package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.in.*;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.FindByIdUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatParticipantService implements AddParticipantsUseCase, RemoveParticipantUseCase, FindParticipantByUserIdAndChatId, FindAllParticipantsUseCase {
    private final ChatParticipantRepository  chatParticipantRepository;
    private final ChatRepository chatRepository;
    
    public ChatParticipantService(ChatParticipantRepository  chatParticipantRepository, ChatRepository chatRepository) {
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatRepository = chatRepository;
    }
    
    @Override
    @Transactional
    public void add(UUID userId, AddParticipantsCommand command) {
        Chat chat = chatRepository.findById(command.chatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat not found");
        }
        
        ChatParticipant myParticipant = chatParticipantRepository.findByUserIdAndChatId(userId, command.chatId());
        if (myParticipant == null) {
            throw new ParticipantNotFoundException("Participant not found");
        }
        
        if (myParticipant.getRole() != ChatParticipantRole.ADMIN) {
            throw new ParticipantAccessDeniedException("You are not allowed to add participants to this chat");
        }
        
        List<ChatParticipant> participants = command.users().stream().map(addUserId -> {
            ChatParticipant participant = new ChatParticipant();
            participant.setUserId(addUserId);
            participant.setChatId(command.chatId());
            
            participant.setRole(ChatParticipantRole.DEFAULT);
            return participant;
        }).toList();
        
        chatParticipantRepository.saveAll(participants);
    }
    
    @Override
    @Transactional
    public void remove(UUID userId, UUID participantId, UUID chatId) {
        ChatParticipant myParticipant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        
        if (myParticipant == null) {
            throw new ChatNotFoundException("Chat not found");
        }
        
        if (myParticipant.getId().equals(participantId)) {
            throw new ParticipantAccessDeniedException("You don't can remove your self");
        }
        
        if (myParticipant.getRole().equals(ChatParticipantRole.DEFAULT)) {
            throw new ParticipantAccessDeniedException("Only ADMIN can remove participants");
        }
        
        ChatParticipant participant = chatParticipantRepository.findById(participantId);
        if (participant == null) {
            throw new ParticipantNotFoundException("Participant to be removed not found");
        }
        
        if (participant.getRole().equals(ChatParticipantRole.ADMIN)) {
            throw new ParticipantAccessDeniedException("You don't can remove another ADMIN");
        }
        
        chatParticipantRepository.deleteById(participantId);
    }
    
    @Override
    public ChatParticipant find(UUID userId, UUID chatId) {
        return chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
    }
    
    @Override
    public List<ChatParticipant> findAll() {
        return List.of();
    }
}
