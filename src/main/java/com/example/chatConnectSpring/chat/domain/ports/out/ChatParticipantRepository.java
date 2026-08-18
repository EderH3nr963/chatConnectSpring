package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ChatParticipantRepository {
    ChatParticipant save(ChatParticipant participant);

    List<ChatParticipant> saveAll(Collection<ChatParticipant> participants);

    ChatParticipant findByUserIdAndChatId(UUID userId, UUID chatId);

    List<ChatParticipant> findByChatId(UUID chatId);

    void deleteById(UUID participantId);

    void deleteByUserIdAndChatId(UUID userId, UUID chatId);
}
