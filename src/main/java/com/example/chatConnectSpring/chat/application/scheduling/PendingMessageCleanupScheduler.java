package com.example.chatConnectSpring.chat.application.scheduling;

import com.example.chatConnectSpring.chat.domain.ports.out.MessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class PendingMessageCleanupScheduler {
    
    private final MessageRepository messageRepository;
    
    public PendingMessageCleanupScheduler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    
    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void execute() {
        messageRepository.deleteOldPendingMessages();
    }
}
