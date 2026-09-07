package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.services.MessageService;
import com.example.chatConnectSpring.chat.domain.ports.in.MessageUseCase;
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

    private final MessageUseCase messageUseCase;
    private final WebSocketPrincipalResolver principalResolver;

    public MessageWebSocketController(
            MessageUseCase messageUseCase,
            WebSocketPrincipalResolver principalResolver
    ) {
        this.messageUseCase = messageUseCase;
        this.principalResolver = principalResolver;
    }
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Principal principal, @Payload WebSocketSendMessageDTO dto) {
        UUID userId = extractUserId(principal);
        messageUseCase.send(userId, new SendMessageCommand(dto.chatId(), dto.content()));
    }

    @MessageMapping("/chat.editMessage")
    public void editMessage(Principal principal, @Payload WebSocketEditMessageDTO dto) {
        UUID userId = extractUserId(principal);
        messageUseCase.edit(userId, new EditMessageCommand(dto.messageId(), dto.chatId(), dto.content()));
    }

    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(Principal principal, @Payload WebSocketDeleteMessageDTO dto) {
        UUID userId = extractUserId(principal);
        messageUseCase.delete(userId, dto.messageId());
    }

    private UUID extractUserId(Principal principal) {
        return principalResolver.resolveUserId(principal);
    }
}
