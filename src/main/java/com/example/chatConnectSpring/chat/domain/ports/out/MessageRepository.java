package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Message update(Message message);
    Message findById(UUID id);
    List<Message> findByChatId(UUID chatId);
    void deleteById(UUID id);
    void deleteByChatId(UUID chatId);
}
