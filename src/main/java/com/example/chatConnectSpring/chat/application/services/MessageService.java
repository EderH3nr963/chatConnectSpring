package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.in.*;
import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidMessageException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.PresenceStateOutputPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MessageService implements
        SendMessageUseCase,
        EditMessageUseCase,
        DeleteMessageUseCase,
        FindMessagesByChatIdUseCase,
        DeleteMessagesByChatIdUseCase {

    private final MessageRepository messageRepository;
    private final MessageNotificationPort messageNotificationPort;
    private final ChatParticipantRepository chatParticipantRepository;
    private final PresenceStateOutputPort presenceStateOutputPort;

    public MessageService(
            MessageRepository messageRepository,
            ChatParticipantRepository chatParticipantRepository,
            MessageNotificationPort messageNotificationPort,
            PresenceStateOutputPort presenceStateOutputPort
    ) {
        this.messageRepository = messageRepository;
        this.messageNotificationPort = messageNotificationPort;
        this.chatParticipantRepository = chatParticipantRepository;
        this.presenceStateOutputPort = presenceStateOutputPort;
    }

    @Override
    @Transactional
    public Message send(UUID userId, SendMessageCommand command) {
        if (command == null || command.content() == null || command.content().trim().isEmpty()) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }

        ChatParticipant myParticipant = chatParticipantRepository.findByUserIdAndChatId(userId, command.chatId());
        if (myParticipant == null) {
            throw new MessageAccessDeniedException("User is not a participant of this chat.");
        }

        Message message = new Message();
        message.setChatId(command.chatId());
        message.setSenderId(myParticipant.getId());
        message.setContent(command.content().trim());

        Message savedMessage = messageRepository.save(message);
        
        List<ChatParticipant> allParticipantInChat = chatParticipantRepository.findByChatId(command.chatId());
        Set<String> usersActiveInChat = presenceStateOutputPort.usersInChat(command.chatId().toString());
        
        List<ChatParticipant> participantsNonActiveInChat = allParticipantInChat.stream()
                .filter(participant -> usersActiveInChat.contains(participant.getUserId().toString()))
                .peek(participant -> participant.setUnreadMessages(participant.getUnreadMessages() + 1)).toList();
        
        messageNotificationPort.notifyMessageSent(savedMessage, participantsNonActiveInChat);
        
        return savedMessage;
    }

    @Override
    @Transactional
    public Message edit(UUID userId, EditMessageCommand command) {
        if (command == null || command.content() == null || command.content().trim().isEmpty()) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }

        Message message = messageRepository.findById(command.messageId());
        if (message == null) {
            throw new MessageNotFoundException("Message not found: " + command.messageId());
        }

        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, command.messageId());
        if (participant == null || !message.getSenderId().equals(participant.getId())) {
            throw new MessageAccessDeniedException("Only the author can edit this message.");
        }

        message.setContent(command.content().trim());
        Message updatedMessage = messageRepository.update(message);
        messageNotificationPort.notifyMessageEdited(updatedMessage);

        return updatedMessage;
    }

    @Override
    @Transactional
    public void delete(UUID userId, UUID messageId) {
        Message message = messageRepository.findById(messageId);
        if (message == null) {
            throw new MessageNotFoundException("Message not found: " + messageId);
        }
        
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, message.getChatId());
        if (participant == null) {
            throw new MessageAccessDeniedException("User is not a participant of this chat.");
        }
        
        boolean isAdmin = participant.getRole() == ChatParticipantRole.ADMIN;
        boolean isAuthor = message.getSenderId().equals(participant.getId());
        
        if (!isAuthor || !isAdmin) {
                throw new MessageAccessDeniedException("Only the message author or chat administrator can delete this message.");
        }

        messageRepository.deleteById(messageId);
        messageNotificationPort.notifyMessageDeleted(message.getChatId(), messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findMessagesByChatId(UUID userId, UUID chatId) {
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        if (participant == null) {
            throw new MessageAccessDeniedException("User is not a participant of this chat.");
        }

        return messageRepository.findByChatId(chatId);
    }

    @Override
    @Transactional
    public void deleteByChatId(UUID chatId) {
        messageRepository.deleteByChatId(chatId);
    }
}
