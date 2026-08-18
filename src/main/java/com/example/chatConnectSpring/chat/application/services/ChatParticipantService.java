package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.in.AddParticipantsUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.RemoveParticipantUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChatParticipantService implements AddParticipantsUseCase, RemoveParticipantUseCase {
    
    @Override
    @Transactional
    public void add(UUID userId, AddParticipantsCommand command) {
    
    }
    
    @Override
    @Transactional
    public void remove(UUID userId, UUID participantId) {
    
    }
}
