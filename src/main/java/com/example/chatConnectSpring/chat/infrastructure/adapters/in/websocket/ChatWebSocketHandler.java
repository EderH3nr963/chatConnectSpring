package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.chat.application.command.DeleteMessageCommand;
import com.example.chatConnectSpring.chat.application.command.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.command.UpdateMessageCommand;
import com.example.chatConnectSpring.chat.application.service.MessageService;
import com.example.chatConnectSpring.chat.application.service.ChatService;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.ChatWebSocketEventRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.ChatWebSocketEventResponseDTO;
import com.example.chatConnectSpring.chat.domain.port.out.ChatWebSocketBroadcaster;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final MessageService messageService;
    private final ChatWebSocketBroadcaster broadcaster;

    public ChatWebSocketHandler(
            ObjectMapper objectMapper,
            ChatService chatService,
            MessageService messageService,
            ChatWebSocketBroadcaster broadcaster
    ) {
        this.objectMapper = objectMapper;
        this.chatService = chatService;
        this.messageService = messageService;
        this.broadcaster = broadcaster;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.getAttributes().putIfAbsent("joinedChatIds", new java.util.HashSet<UUID>());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ChatWebSocketEventRequestDTO request = objectMapper.readValue(message.getPayload(), ChatWebSocketEventRequestDTO.class);
        if (request.event() == null) {
            throw new IllegalArgumentException("Evento é necessário");
        }

        switch (request.event()) {
            case JOIN_CHAT -> handleJoinChat(session, request);
            case SEND_MESSAGE -> handleSendMessage(session, request);
            case UPDATE_MESSAGE -> handleUpdateMessage(session, request);
            case DELETE_MESSAGE -> handleDeleteMessage(session, request);
        }
    }

    private void handleJoinChat(WebSocketSession session, ChatWebSocketEventRequestDTO request) throws IOException {
        chatService.addParticipant(request.chatId(), request.userId());
        broadcaster.registerChatSession(request.chatId(), session);
        broadcast(request.chatId(), ChatWebSocketEventType.JOIN_CHAT, request.userId(), null, null);
    }

    private void handleSendMessage(WebSocketSession session, ChatWebSocketEventRequestDTO request) throws IOException {
        var message = messageService.sendMessage(new SendMessageCommand(request.chatId(), request.userId(), request.content()));
        broadcast(request.chatId(), ChatWebSocketEventType.SEND_MESSAGE, message.getUserId(), message.getId(), message.getContent());
    }

    private void handleUpdateMessage(WebSocketSession session, ChatWebSocketEventRequestDTO request) throws IOException {
        var message = messageService.updateMessage(new UpdateMessageCommand(request.chatId(), request.messageId(), request.userId(), request.content()));
        broadcast(request.chatId(), ChatWebSocketEventType.UPDATE_MESSAGE, message.getUserId(), message.getId(), message.getContent());
    }

    private void handleDeleteMessage(WebSocketSession session, ChatWebSocketEventRequestDTO request) throws IOException {
        var message = messageService.deleteMessage(new DeleteMessageCommand(request.chatId(), request.messageId(), request.userId()));
        broadcast(request.chatId(), ChatWebSocketEventType.DELETE_MESSAGE, message.getUserId(), message.getId(), message.getContent());
    }

    private void broadcast(UUID chatId, ChatWebSocketEventType event, UUID userId, UUID messageId, String content) throws IOException {
        broadcaster.broadcastToChat(chatId, new ChatWebSocketEventResponseDTO(
                event,
                chatId,
                messageId,
                userId,
                content,
                OffsetDateTime.now()
        ));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broadcaster.unregisterSession(session);
    }
}
