package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.events.MessageSentEvent;
import com.example.chatConnectSpring.chat.application.exceptions.MessageNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.model.message.MessageStatus;
import com.example.chatConnectSpring.chat.domain.ports.in.ConfirmMessageAttachmentUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.EventPublisherPort;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfirmMessageAttachmentService implements ConfirmMessageAttachmentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmMessageAttachmentService.class);
    private final MessageRepository messageRepository;
    private final EventPublisherPort eventPublisher;
    
    public ConfirmMessageAttachmentService(MessageRepository messageRepository, EventPublisherPort eventPublisher) {
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public void execute(UUID messageId, String objectKey, long fileSize, String mimeType) {
        if (objectKey == null || !objectKey.startsWith("uploads/chat/message")) {
            return;
        }
        
        Message message = messageRepository.findById(messageId);
        if (message == null) {
            throw new MessageNotFoundException("Message with id " + messageId + " not found");
        }
        
        if (fileSize > 0) message.setFileSize(fileSize);
        if (mimeType != null && !mimeType.isBlank()) message.setFileMimeType(mimeType);
        message.setStatus(MessageStatus.SENT);
        
        messageRepository.update(message);
        eventPublisher.publishEvent(new MessageSentEvent(message.getId()));
    }
}
