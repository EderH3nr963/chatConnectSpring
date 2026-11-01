package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.events.MessageSentEvent;
import com.example.chatConnectSpring.chat.application.exceptions.*;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.model.message.MessageStatus;
import com.example.chatConnectSpring.chat.domain.model.message.SendMessageResult;
import com.example.chatConnectSpring.chat.domain.ports.in.MessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.*;
import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
public class MessageService implements
        MessageUseCase {

    private final MessageRepository messageRepository;
    private final MessageNotificationPort messageNotificationPort;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatResolver chatResolver;
    private final EventPublisherPort eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final StoragePort storagePort;
    private final S3CleanupOutboxRepository s3CleanupOutboxRepository;

    public MessageService(
            MessageRepository messageRepository,
            ChatParticipantRepository chatParticipantRepository,
            MessageNotificationPort messageNotificationPort,
            ChatResolver chatResolver,
            EventPublisherPort eventPublisher,
            StoragePort storagePort,
            S3CleanupOutboxRepository s3CleanupOutboxRepository
    ) {
        this.messageRepository = messageRepository;
        this.messageNotificationPort = messageNotificationPort;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatResolver = chatResolver;
        this.eventPublisher = eventPublisher;
        this.storagePort = storagePort;
        this.s3CleanupOutboxRepository = s3CleanupOutboxRepository;
    }

    @Override
    @Transactional
    public SendMessageResult send(UUID userId, SendMessageCommand command) {
        if (command == null) {
            throw new InvalidMessageException("Message content cannot be empty.");
        }
        log.info("Send message: userId={} chatId={} type={} hasAttachment={}", userId, command.chatId(), command.type(), command.attachmentInfo() != null);
        command.validate();
        
        UUID chatId = chatResolver.resolveChatId(userId, command);
        
        ChatParticipant sender = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        if (sender == null) {
            throw new MessageAccessDeniedException("User is not a participant of this chat.");
        }
        
        if (command.replyId() != null) {
            Message reply = messageRepository.findById(command.replyId());
            if (reply == null || !reply.getChatId().equals(chatId)) {
                throw new InvalidMessageException("Reply message not found in this chat.");
            }
        }
        Message message = Message.create(chatId, userId, command.type(), command.content(), command.attachmentInfo(), command.replyId());
        log.debug("Message created id={} chatId={} status={} replyId={}", message.getId(), chatId, message.getStatus(), command.replyId());
        
        Message savedMessage = messageRepository.save(message);
        if (savedMessage.getReplyId() != null) {
            Message reloaded = messageRepository.findById(savedMessage.getId());
            if (reloaded != null) savedMessage = reloaded;
        }
        log.info("Message saved id={} chatId={} status={}", savedMessage.getId(), chatId, savedMessage.getStatus());
        
        if (savedMessage.getStatus() == MessageStatus.SENT) {
            log.debug("Publishing MessageSentEvent for messageId={}", savedMessage.getId());
            eventPublisher.publishEvent(new MessageSentEvent(savedMessage.getId()));
            return new SendMessageResult(savedMessage, null);
        }
        
        log.debug("Generating presigned upload URL for messageId={} key={}", savedMessage.getId(), savedMessage.getFileS3Key());
        String presignedUploadUrl = storagePort.generatePresignedUploadUrl(
                savedMessage.getFileS3Key(),
                savedMessage.getFileMimeType(),
                Duration.ofMinutes(15)
        );
        return new SendMessageResult(savedMessage, presignedUploadUrl);
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

        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, command.chatId());
        // senderId no dominio hoje é participantId (authorParticipant.getId()) - comparar com participant.getId() quando houver, senão com userId
        UUID authorId = message.getSenderId();
        boolean isAuthor = authorId != null && (authorId.equals(participant != null ? participant.getId() : null) || authorId.equals(participant != null ? participant.getUserId() : null) || authorId.equals(userId));
        if (participant == null || !isAuthor) {
            throw new MessageAccessDeniedException("Only the author can edit this message.");
        }
        
        message.setContent(command.content().trim());
        
        Message updatedMessage = messageRepository.update(message);
        log.info("Message edited id={} chatId={} by userId={}", message.getId(), message.getChatId(), userId);
        updatedMessage.setUpdatedAt(updatedMessage.getUpdatedAt());
        
        messageNotificationPort.notifyMessageEdited(message);

        return message;
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
        UUID senderId = message.getSenderId();
        boolean isAuthor = senderId != null && (senderId.equals(participant.getId()) || senderId.equals(participant.getUserId()) || senderId.equals(userId));
        
        if (!isAuthor && !isAdmin) {
            throw new MessageAccessDeniedException("Only the message author or chat administrator can delete this message.");
        }

        String s3Key = message.getFileS3Key();
        UUID msgChatId = message.getChatId();
        log.info("Message deleted id={} chatId={} by userId={} hasAttachment={}", messageId, msgChatId, userId, s3Key != null && !s3Key.isBlank());
        messageRepository.deleteById(messageId);
        
        if (s3Key != null && !s3Key.isBlank()) {
            s3CleanupOutboxRepository.save(S3CleanupTask.pending(s3Key, messageId, msgChatId));
            log.debug("Outbox enqueued for s3Key={} messageId={}", s3Key, messageId);
        }
        
        messageNotificationPort.notifyMessageDeleted(msgChatId, messageId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Slice<Message> findMessagesByChatId(UUID userId, UUID chatId, Pageable pageable) {
        ChatParticipant participant = chatParticipantRepository.findByUserIdAndChatId(userId, chatId);
        if (participant == null) {
            throw new MessageAccessDeniedException("User is not a participant of this chat.");
        }
        
        return messageRepository.findByChatId(chatId, pageable);
    }
    
    @Override
    public void deleteByChatId(UUID chatId) {
        messageRepository.deleteByChatId(chatId);
    }
}
