package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageRepository;
import com.example.chatConnectSpring.chat.infrastructure.mapper.MessageMapper;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public List<Message> findByChatId(UUID chatId) {
        return messageJpaRepository.findByChatIdOrderByCreatedAtAsc(chatId)
                .stream()
                .map(MessageMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        messageJpaRepository.deleteById(id);
    }

    @Override
    public void deleteByChatId(UUID chatId) {
        messageJpaRepository.deleteByChatId(chatId);
    }
}
