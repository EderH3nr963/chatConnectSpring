package com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.events.MessageSentEvent;
import com.example.chatConnectSpring.chat.domain.model.message.MessageStatus;
import com.example.chatConnectSpring.chat.domain.model.message.SendMessageResult;
import com.example.chatConnectSpring.chat.domain.ports.in.MessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.EventPublisherPort;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.UploadAckResponse;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.WebSocketDeleteMessageDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.WebSocketEditMessageDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.websocket.dto.WebSocketSendMessageDTO;
import com.example.chatConnectSpring.auth.infrastructure.security.websocket.WebSocketPrincipalResolver;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
public class MessageWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(MessageWebSocketController.class);

    private final MessageUseCase messageUseCase;
    private final WebSocketPrincipalResolver principalResolver;
    private final EventPublisherPort eventPublisherPort;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageWebSocketController(
            MessageUseCase messageUseCase,
            WebSocketPrincipalResolver principalResolver,
            SimpMessagingTemplate messagingTemplate,
            EventPublisherPort eventPublisherPort
    ) {
        this.messageUseCase = messageUseCase;
        this.eventPublisherPort = eventPublisherPort;
        this.principalResolver = principalResolver;
        this.messagingTemplate = messagingTemplate;
    }
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Principal principal, @Payload WebSocketSendMessageDTO dto) {
        UUID userId = extractUserId(principal);
        SendMessageResult result = messageUseCase.send(userId, new SendMessageCommand(dto.targetId(), dto.chatId(), dto.content(), dto.attachmentInfo(),dto.messageType(), dto.replyId()));
        
        if (!result.requiresUpload()) {
            return;
        }
        
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/upload-ack",
                new UploadAckResponse(dto.clientMessageId(), result.presignedUploadUrl())
        );
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
