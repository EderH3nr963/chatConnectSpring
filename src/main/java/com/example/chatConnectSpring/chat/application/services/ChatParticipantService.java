package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatTypeOperationException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.in.ChatParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatParticipantService implements ChatParticipantUseCase {
    private final ChatParticipantRepository  chatParticipantRepository;
    private final ChatNotificationPort chatNotificationPort;
    private final ChatRepository chatRepository;
    
    public ChatParticipantService(ChatParticipantRepository  chatParticipantRepository, ChatNotificationPort chatNotificationPort, ChatRepository chatRepository) {
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatNotificationPort = chatNotificationPort;
        this.chatRepository = chatRepository;
    }
    
    @Override
    @Transactional
    public void add(UUID userId, AddParticipantsCommand command) {
        Chat chat = chatRepository.findById(command.chatId());
        if (chat == null)
            throw new ChatNotFoundException("Chat not found");
        
        if (chat.getChatType() != ChatTypeEnum.GROUP)
            throw new InvalidChatTypeOperationException("Cannot add participants to a private chat");
        
        
        ChatParticipant myParticipant = chatParticipantRepository.findByUserIdAndChatId(userId, command.chatId());
        if (myParticipant == null)
            throw new ParticipantNotFoundException("Participant not found");
        
        if (myParticipant.getRole() != ChatParticipantRole.ADMIN)
            throw new ParticipantAccessDeniedException("You are not allowed to add participants to this chat");
        
        
        List<ChatParticipant> participants = command.users().stream().map(addUserId -> {
            ChatParticipant participant = new ChatParticipant();
            participant.setUserId(addUserId);
            participant.setChatId(command.chatId());
            
            participant.setRole(ChatParticipantRole.DEFAULT);
            return participant;
        }).toList();
        
        chatParticipantRepository.saveAll(participants);
        
        command.users().forEach(participantUserId -> chatNotificationPort.notifyJoinedChat(participantUserId, chat));
    }
    
    @Override
    @Transactional
    public void remove(UUID userId, UUID participantId, UUID chatId) {
        ChatParticipant myParticipant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        
        if (myParticipant == null)
            throw new ChatNotFoundException("Chat not found");
        
        if (myParticipant.getId().equals(participantId))
            throw new ParticipantAccessDeniedException("You don't can remove your self");
        
        if (myParticipant.getRole().equals(ChatParticipantRole.DEFAULT))
            throw new ParticipantAccessDeniedException("Only ADMIN can remove participants");
        
        ChatParticipant participant = chatParticipantRepository.findById(participantId);
        if (participant == null || !participant.getChatId().equals(chatId))
            throw new ParticipantNotFoundException("Participant to be removed not found");
        
        if (participant.getRole().equals(ChatParticipantRole.ADMIN))
            throw new ParticipantAccessDeniedException("You don't can remove another ADMIN");
        
        chatParticipantRepository.deleteById(participantId);
        
        chatNotificationPort.notifyLeftChat(userId, chatId);
    }
}
