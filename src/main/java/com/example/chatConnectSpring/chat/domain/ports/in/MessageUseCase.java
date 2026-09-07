package com.example.chatConnectSpring.chat.domain.ports.in;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageUseCase {
    Message send(UUID userId, SendMessageCommand command);
    List<Message> findMessagesByChatId(UUID userId, UUID chatId);
    Message edit(UUID userId, EditMessageCommand command);
    void delete(UUID userId, UUID messageId);
    void deleteByChatId(UUID chatId);
    
}
