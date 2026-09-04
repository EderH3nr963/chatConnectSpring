package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.domain.ports.in.DeleteMessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.EditMessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.in.SendMessageUseCase;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.WebSocketDeleteMessageDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.WebSocketEditMessageDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.WebSocketSendMessageDTO;
import com.example.chatConnectSpring.shared.security.websocket.WebSocketPrincipalResolver;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class MessageWebSocketController {

    private final SendMessageUseCase sendMessageUseCase;
    private final EditMessageUseCase editMessageUseCase;
    private final DeleteMessageUseCase deleteMessageUseCase;
    private final WebSocketPrincipalResolver principalResolver;

    public MessageWebSocketController(
            SendMessageUseCase sendMessageUseCase,
            EditMessageUseCase editMessageUseCase,
            DeleteMessageUseCase deleteMessageUseCase,
            WebSocketPrincipalResolver principalResolver
    ) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.editMessageUseCase = editMessageUseCase;
        this.deleteMessageUseCase = deleteMessageUseCase;
        this.principalResolver = principalResolver;
    }
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Principal principal, @Payload WebSocketSendMessageDTO dto) {
        UUID userId = extractUserId(principal);
        sendMessageUseCase.send(userId, new SendMessageCommand(dto.chatId(), dto.content()));
    }

    @MessageMapping("/chat.editMessage")
    public void editMessage(Principal principal, @Payload WebSocketEditMessageDTO dto) {
        UUID userId = extractUserId(principal);
        editMessageUseCase.edit(userId, new EditMessageCommand(dto.messageId(), dto.content()));
    }

    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(Principal principal, @Payload WebSocketDeleteMessageDTO dto) {
        UUID userId = extractUserId(principal);
        deleteMessageUseCase.delete(userId, dto.messageId());
    }

    private UUID extractUserId(Principal principal) {
        return principalResolver.resolveUserId(principal);
    }
}
