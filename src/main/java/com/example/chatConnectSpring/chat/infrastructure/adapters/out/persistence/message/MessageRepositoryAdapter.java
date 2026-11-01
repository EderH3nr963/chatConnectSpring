package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageRepository;
import com.example.chatConnectSpring.chat.infrastructure.mapper.MessageMapper;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

@Component
public class MessageRepositoryAdapter implements MessageRepository {

    private final MessageJpaRepository messageJpaRepository;

    public MessageRepositoryAdapter(MessageJpaRepository messageJpaRepository) {
        this.messageJpaRepository = messageJpaRepository;
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            return null;
        }

        MessageEntity entity = MessageMapper.toEntity(message);
        return MessageMapper.toDomain(messageJpaRepository.save(entity));
    }

    @Override
    public Message update(Message message) {
        return save(message);
    }

    @Override
    public Message findById(UUID id) {
        return messageJpaRepository.findById(id)
                .map(MessageMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Slice<Message> findByChatId(UUID chatId, Pageable pageable) {
        return messageJpaRepository.findByChatId(chatId, pageable)
                .map(MessageMapper::toDomain);
    }

    @Override
    public java.util.Optional<Message> findLastMessageByChatId(UUID chatId) {
        return messageJpaRepository.findFirstByChatIdOrderByCreatedAtDesc(chatId)
                .map(MessageMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        messageJpaRepository.deleteById(id);
    }

    @Override
    public java.util.List<Message> findWithAttachmentByChatId(UUID chatId) {
        return messageJpaRepository.findWithAttachmentByChatId(chatId).stream().map(MessageMapper::toDomain).toList();
    }

    @Override
    public void deleteByChatId(UUID chatId) {
        messageJpaRepository.deleteByChatId(chatId);
    }
    
    @Override
    public void deleteOldPendingMessages() {
        messageJpaRepository.deleteOldPendingMessages();
    }
}
