package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.CreatePrivateChatCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.in.*;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import com.example.chatConnectSpring.chat.application.exceptions.ChatAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ChatService implements
        CreateGroupChatUseCase,
        CreatePrivateChatUseCase,
        FindChatByIdUseCase,
        FindChatsByUserIdUseCase,
        UpdateChatUseCase,
        DeleteChatUseCase {
    
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
    public Chat createGroupChat(
            UUID userId,
            CreateGroupChatCommand command
    ) {
        Chat chat = new Chat();
        
        chat.setTitle(command.title());
        chat.setDescription(command.description());
        chat.setChatType(ChatTypeEnum.GROUP);
        
        chat = chatRepository.save(chat);
        
        Set<ChatParticipant> participants = new HashSet<>();
        
        ChatParticipant adminParticipant = new ChatParticipant();
        adminParticipant.setRole(ChatParticipantRole.ADMIN);
        adminParticipant.setUserId(userId);
        adminParticipant.setChatId(chat.getId());
        participants.add(adminParticipant);
        
        for (UUID participantUserId: command.participants()) {
            ChatParticipant participant = new ChatParticipant();
            participant.setUserId(participantUserId);
            participant.setChatId(chat.getId());
            participant.setRole(ChatParticipantRole.DEFAULT);
            
            participants.add(participant);
        }
        
        chatParticipantRepository.saveAll(participants);
        
        return chat;
    }
    
    @Override
    @Transactional
    public Chat createPrivateChat(
            UUID userId,
            CreatePrivateChatCommand command
    ) {
        
        if (command.participants() == null ||
                command.participants().users() == null ||
                command.participants().users().size() != 1) {
            
            throw new InvalidChatException(
                    "A private chat must have exactly one other participant."
            );
        }
        
        UUID participantId = command.participants()
                .users()
                .get(0);
        
        if (userId.equals(participantId)) {
            throw new InvalidChatException(
                    "A private chat cannot be created with yourself."
            );
        }
        
        Chat chat = new Chat();
        
        chat.setChatType(ChatTypeEnum.PRIVATE);
        
        chat = chatRepository.save(chat);
        
        ChatParticipant adminParticipant = new ChatParticipant();
        adminParticipant.setUserId(userId);
        adminParticipant.setChatId(chat.getId());
        adminParticipant.setRole(ChatParticipantRole.ADMIN);
        chatParticipantRepository.save(adminParticipant);

        ChatParticipant participant = new ChatParticipant();
        participant.setUserId(participantId);
        participant.setChatId(chat.getId());
        participant.setRole(ChatParticipantRole.DEFAULT);
        participant = chatParticipantRepository.save(participant);

        chat.setTitle(participant.getName());

        return chatRepository.update(chat);
    }
    
    @Override
    public Chat findChatById(
            UUID userId,
            UUID chatId
    ) {
        
        ChatParticipant participant =
                chatParticipantRepository.findByUserIdAndChatId(
                        userId,
                        chatId
                );
        
        if (participant == null) {
            throw new ChatAccessDeniedException(
                    "User does not have access to this chat."
            );
        }
        
        Chat chat = chatRepository.findById(chatId);
        
        if (chat == null) {
            throw new ChatNotFoundException(
                    "Chat not found: " + chatId
            );
        }
        
        return chat;
    }
    
    @Override
    public List<Chat> findChatsByUSerId(UUID userId) {
        return chatRepository.findChatByUserId(userId);
    }
    
    @Override
    @Transactional
    public Chat update(
            UUID userId,
            UpdateChatCommand command
    ) {
        
        ChatParticipant participant =
                chatParticipantRepository.findByUserIdAndChatId(
                        userId,
                        command.chatId()
                );
        
        if (participant == null) {
            throw new ChatAccessDeniedException(
                    "User does not have access to this chat."
            );
        }
        
        if (participant.getRole() != ChatParticipantRole.ADMIN) {
            throw new ChatAccessDeniedException(
                    "Only administrators can update this chat."
            );
        }
        
        Chat chat = chatRepository.findById(command.chatId());

        if (chat == null) {
            throw new ChatNotFoundException(
                    "Chat not found: " + command.chatId()
            );
        }

        if (chat.getChatType() != ChatTypeEnum.GROUP) {
            return chat;
        }
        
        chat.setTitle(command.title());
        chat.setDescription(command.description());
        
        return chatRepository.update(chat);
    }
    
    @Override
    @Transactional
    public void delete(
            UUID userId,
            UUID chatId
    ) {
        
        ChatParticipant participant =
                chatParticipantRepository.findByUserIdAndChatId(
                        userId,
                        chatId
                );
        
        if (participant == null) {
            throw new ChatAccessDeniedException(
                    "User does not have access to this chat."
            );
        }
        
        if (participant.getRole() != ChatParticipantRole.ADMIN) {
            throw new ChatAccessDeniedException(
                    "Only administrators can delete this chat."
            );
        }
        
        Chat chat = chatRepository.findById(chatId);
        
        if (chat == null) {
            throw new ChatNotFoundException(
                    "Chat not found: " + chatId
            );
        }
        
        chatRepository.delete(chatId);
    }
}
