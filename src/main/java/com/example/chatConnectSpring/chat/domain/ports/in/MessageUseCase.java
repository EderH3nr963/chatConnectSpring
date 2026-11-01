package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.model.message.SendMessageResult;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface MessageUseCase {
    SendMessageResult send(UUID userId, SendMessageCommand command);
    Slice<Message> findMessagesByChatId(UUID userId, UUID chatId, Pageable pageable);
    Message edit(UUID userId, EditMessageCommand command);
    void delete(UUID userId, UUID messageId);
    void deleteByChatId(UUID chatId);
}
