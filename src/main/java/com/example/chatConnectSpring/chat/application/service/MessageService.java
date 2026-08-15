package com.example.chatConnectSpring.chat.application.service;

import com.example.chatConnectSpring.chat.application.command.DeleteMessageCommand;
import com.example.chatConnectSpring.chat.application.command.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.command.UpdateMessageCommand;
import com.example.chatConnectSpring.chat.domain.model.ChatMessage;
import com.example.chatConnectSpring.chat.domain.port.in.DeleteMessageUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.SendMessageUseCase;
import com.example.chatConnectSpring.chat.domain.port.in.UpdateMessageUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.MessageRepository;
import com.example.chatConnectSpring.chat.domain.port.out.ChatRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class MessageService implements SendMessageUseCase, UpdateMessageUseCase, DeleteMessageUseCase {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public MessageService(ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public ChatMessage sendMessage(UUID chatId, UUID userId, String content) {
        if (chatId == null) throw new IllegalArgumentException("Chat ID é necessário");
        if (userId == null) throw new IllegalArgumentException("Usuário ID é necessário");
        if (content == null || content.isBlank()) throw new IllegalArgumentException("Conteúdo é necessário");
        if (chatRepository.findById(chatId) == null) throw new IllegalArgumentException("Chat não encontrado");

        ChatMessage message = new ChatMessage();
        message.setId(UUID.randomUUID());
        message.setChatId(chatId);
        message.setUserId(userId);
        message.setContent(content.trim());
        message.setCreatedAt(OffsetDateTime.now());
        message.setUpdatedAt(message.getCreatedAt());
        message.setDeleted(false);
        
        return messageRepository.save(message);
    }

    public ChatMessage sendMessage(SendMessageCommand command) { return sendMessage(command.chatId(), command.userId(), command.content()); }

    @Override
    public ChatMessage updateMessage(UUID chatId, UUID messageId, UUID userId, String content) {
        if (messageId == null) throw new IllegalArgumentException("Mensagem ID é necessário");
        
        ChatMessage message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Mensagem não encontrada"));
        if (!message.getChatId().equals(chatId)) throw new IllegalArgumentException("Mensagem não pertence ao chat");
        
        message.setContent(content == null ? null : content.trim());
        message.setUpdatedAt(OffsetDateTime.now());
        
        return messageRepository.save(message);
    }

    public ChatMessage updateMessage(UpdateMessageCommand command) { return updateMessage(command.chatId(), command.messageId(), command.userId(), command.content()); }

    @Override
    public ChatMessage deleteMessage(UUID chatId, UUID messageId, UUID userId) {
        ChatMessage message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Mensagem não encontrada"));
        if (!message.getChatId().equals(chatId)) throw new IllegalArgumentException("Mensagem não pertence ao chat");
        
        message.setDeleted(true);
        message.setContent(null);
        message.setUpdatedAt(OffsetDateTime.now());
        
        return messageRepository.save(message);
    }

    public ChatMessage deleteMessage(DeleteMessageCommand command) { return deleteMessage(command.chatId(), command.messageId(), command.userId()); }
}
