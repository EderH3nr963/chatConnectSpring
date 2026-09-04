package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.in.ManagePresenceUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.PresenceStateOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ManagePresenceService implements ManagePresenceUseCase {
    private final ChatParticipantRepository chatParticipantRepository;
    private final PresenceStateOutputPort presenceStateOutputPort;
    
    public ManagePresenceService(ChatParticipantRepository chatParticipantRepository, PresenceStateOutputPort presenceStateOutputPort) {
        this.chatParticipantRepository = chatParticipantRepository;
        this.presenceStateOutputPort = presenceStateOutputPort;
    }
    
    @Override
    @Transactional
    public void enterChat(UUID userId, UUID chatId) {
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        if (participant == null) {
            throw new ParticipantNotFoundException("You are not logged in");
        }
        
        participant.setUnreadMessages(0);
        chatParticipantRepository.save(participant);
        
        presenceStateOutputPort.setUserActiveInChat(chatId.toString(), userId.toString());
    }
    
    @Override
    public void exitChat(UUID chatId, UUID userId) {
        presenceStateOutputPort.clearUserActiveChat(chatId.toString(), userId.toString());
    }
}
