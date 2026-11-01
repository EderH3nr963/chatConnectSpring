package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.GenerateGroupImageUploadUrlCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.in.ChatUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.*;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;
import com.example.chatConnectSpring.shared.utils.GetExtensionFromContentType;
import com.example.chatConnectSpring.user.application.service.UserService;
import com.example.chatConnectSpring.user.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ChatService implements ChatUseCase {
    
    private final ChatRepository chatRepository;
    private final StoragePort storagePort;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserService userService;
    private final EventPublisherPort eventPublisher;
    
    public ChatService(
            ChatRepository chatRepository,
            ChatParticipantRepository chatParticipantRepository,
            UserService userService,
            StoragePort storagePort,
            EventPublisherPort eventPublisher
    ) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.userService = userService;
        this.storagePort = storagePort;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public Chat createGroupChat(
            UUID userId,
            CreateGroupChatCommand command
    ) {
        command.validate(userId);
        List<UUID> requestedParticipantIds = command.memberIds();
        
        List<User> existingUsers = userService.findUsersByUsersId(requestedParticipantIds);
        Set<UUID> existingUserIds = existingUsers.stream().map(User::getId).collect(java.util.stream.Collectors.toSet());
        if (!existingUserIds.containsAll(requestedParticipantIds)) {
            throw new InvalidChatException("One or more memberIds do not exist.");
        }

        Chat chat = new Chat();
        
        chat.setName(command.name());
        chat.setDescription(command.description());
        chat.setChatType(ChatTypeEnum.GROUP);
        
        chat = chatRepository.save(chat);
        
        Set<ChatParticipant> participants = new HashSet<>();
        
        ChatParticipant adminParticipant = new ChatParticipant();
        adminParticipant.setRole(ChatParticipantRole.ADMIN);
        adminParticipant.setUserId(userId);
        adminParticipant.setChatId(chat.getId());
        participants.add(adminParticipant);
        
        for (UUID participantUserId : requestedParticipantIds) {
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
            UUID anotherUserId
    ) {
        
        if (userId.equals(anotherUserId))
            throw new InvalidChatException("A private chat cannot be created with yourself.");
        
        Chat chat = new Chat();
        chat.setChatType(ChatTypeEnum.PRIVATE);
        
        chat = chatRepository.save(chat);
        
        User myUser = userService.findById(userId);
        if (myUser == null)
            throw new InvalidChatException("User does not exist.");
        
        User anotherUser = userService.findById(anotherUserId);
        if (anotherUser == null)
            throw new InvalidChatException("User does not exist.");
        
        ChatParticipant participant = new ChatParticipant();
        participant.setUserId(myUser.getId());
        participant.setUsername(myUser.getUsername());
        participant.setChatId(chat.getId());
        participant.setRole(ChatParticipantRole.DEFAULT);
        chatParticipantRepository.save(participant);
        
        ChatParticipant anotherParticipant = new ChatParticipant();
        anotherParticipant.setUserId(anotherUser.getId());
        anotherParticipant.setChatId(chat.getId());
        anotherParticipant.setUsername(anotherUser.getUsername());
        anotherParticipant.setRole(ChatParticipantRole.DEFAULT);
        chatParticipantRepository.save(anotherParticipant);
        
        chat.setName(anotherUser.getUsername());
        
        return chat;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Chat findChatById(
            UUID userId,
            UUID chatId
    ) {
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        
        if (participant == null)
            throw new ChatAccessDeniedException("User does not have access to this chat.");
        
        Chat chat = chatRepository.findById(chatId);
        
        if (chat == null)
            throw new ChatNotFoundException("Chat not found: " + chatId);
        
        if (chat.getChatType().equals(ChatTypeEnum.GROUP))
            return chat;
        
        ChatParticipant anotherParticipant = chatParticipantRepository.findByChatId(chat.getId()).stream()
                .filter(participantOfChat -> !participantOfChat.getUserId().equals(userId))
                .toList()
                .get(0);
        
        chat.setName(anotherParticipant.getUsername());
        chat.setAvatarKey(anotherParticipant.getUserAvatarKey());
        
        return chat;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatSummaryDto> findChatsSummaryByUserId(UUID userId) {
        return chatRepository.findChatSummaries(userId);
    }
    
    @Override
    @Transactional
    public Chat update(
            UUID userId,
            UpdateChatCommand command
    ) {
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, command.chatId());
        
        if (participant == null)
            throw new ChatAccessDeniedException("User does not have access to this chat.");
        
        if (participant.getRole() != ChatParticipantRole.ADMIN)
            throw new ChatAccessDeniedException("Only administrators can update this chat.");
        
        Chat chat = chatRepository.findById(command.chatId());
        if (chat == null) throw new ChatNotFoundException("Chat not found: " + command.chatId());
        
        if (chat.getChatType() != ChatTypeEnum.GROUP)
            return chat;
        
        chat.setName(command.name());
        chat.setDescription(command.description());
        
        eventPublisher.publishEvent(chat);
        return chatRepository.update(chat);
    }
    
    @Override
    @Transactional
    public void delete(
            UUID userId,
            UUID chatId
    ) {
        
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        
        if (participant == null)
            throw new ChatAccessDeniedException("User does not have access to this chat.");
        
        if (participant.getRole() != ChatParticipantRole.ADMIN)
            throw new ChatAccessDeniedException("Only administrators can delete this chat.");
        
        Chat chat = chatRepository.findById(chatId);
        
        if (chat == null)
            throw new ChatNotFoundException("Chat not found: " + chatId);
        
        eventPublisher.publishEvent(chatId);
        chatRepository.delete(chatId);
    }
    
    @Override
    public String generateGroupImageUploadUrl(UUID userId, UUID chatId, GenerateGroupImageUploadUrlCommand command) {
        Chat chat = chatRepository.findById(chatId);
        if (chat == null) {
            throw new ChatNotFoundException("Chat not found: " + chatId);
        }
        
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        if (participant == null || participant.getRole() != ChatParticipantRole.ADMIN) {
            throw new ParticipantAccessDeniedException("User does not have access to this chat.");
        }
        
        String extension = GetExtensionFromContentType.get(command.contentType());
        String objectKey = String.format("uploads/chat/avatar/%s%s", chatId, extension);
        
        return storagePort.generatePresignedUploadUrl(objectKey, command.contentType(), Duration.ofMinutes(10));
    }
}
