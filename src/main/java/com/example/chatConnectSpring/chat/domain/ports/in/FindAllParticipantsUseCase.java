package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.util.List;

public interface FindAllParticipantsUseCase {
    List<ChatParticipant> findAll();
}
