package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.message.Message;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageRepository {
    Message save(Message message);
    Message update(Message message);
    Message findById(UUID id);
    Slice<Message> findByChatId(UUID chatId, Pageable pageable);
    java.util.Optional<Message> findLastMessageByChatId(UUID chatId);
    void deleteById(UUID id);
    void deleteByChatId(UUID chatId);
    void deleteOldPendingMessages();
    java.util.List<Message> findWithAttachmentByChatId(UUID chatId);
}
