package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.in.AddParticipantsUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.RemoveParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import com.example.chatConnectSpring.usuario.domain.port.in.FindByIdUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatParticipantService implements AddParticipantsUseCase, RemoveParticipantUseCase {
    private FindByIdUseCase findByIdUseCase;
    private ChatParticipantRepository  chatParticipantRepository;
    private RemoveParticipantUseCase removeParticipantUseCase;
    private ChatRepository chatRepository;
    
    @Override
    @Transactional
    public void add(UUID userId, AddParticipantsCommand command) {
        List<ChatParticipant> participants = command.users().stream().map(addUserId -> {
            ChatParticipant participant = new ChatParticipant();
            participant.setUserId(addUserId);
            participant.setChatId(command.chatId());
            
            Usuario usuario = findByIdUseCase.findById(userId);
            participant.setName(usuario.getUsername());
            
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
            throw new ParticipantAccessDeniedException("You dont can remove your self");
        }
        
        if (myParticipant.getRole().equals(ChatParticipantRole.DEFAULT)) {
            throw new ParticipantAccessDeniedException("Only ADMIN can remove participants");
        }
        
        ChatParticipant participant = chatParticipantRepository.findById(participantId);
        if (participant == null) {
            throw new ParticipantNotFoundException("Participant to be not found");
        }
        
        if (participant.getRole().equals(ChatParticipantRole.ADMIN)) {
            throw new ParticipantAccessDeniedException("You don't can remove another ADMIN");
        }
        
        chatParticipantRepository.deleteById(participantId);
    }
}
