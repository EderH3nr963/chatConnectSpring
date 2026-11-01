package com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging.strategy;

import com.example.chatConnectSpring.chat.domain.ports.in.ConfirmMessageAttachmentUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageAttachmentS3Strategy implements S3MediaProcessorStrategy {
    
    private static final String MESSAGE_ATTACHMENT_PREFIX = "uploads/chat/message";
    private final ConfirmMessageAttachmentUseCase confirmMessageAttachmentUseCase;
    
    public MessageAttachmentS3Strategy(ConfirmMessageAttachmentUseCase confirmMessageAttachmentUseCase) {
        this.confirmMessageAttachmentUseCase = confirmMessageAttachmentUseCase;
    }
    
    @Override
    public boolean supports(String objectKey) {
        return objectKey != null && objectKey.startsWith(MESSAGE_ATTACHMENT_PREFIX);
    }
    
    @Override
    public void process(String objectKey, JsonNode recordNode) {
        UUID messageId = extractMessageIdFromKey(objectKey);
        // LocalStack/AWS event: size em s3.object.size, contentType pode vir em diferentes campos
        JsonNode objectNode = recordNode.path("s3").path("object");
        long fileSize = objectNode.path("size").asLong(0);
        String mimeType = null;
        if (objectNode.has("contentType")) mimeType = objectNode.path("contentType").asText(null);
        else if (objectNode.has("content-type")) mimeType = objectNode.path("content-type").asText(null);
        else if (objectNode.has("mimeType")) mimeType = objectNode.path("mimeType").asText(null);
        else if (objectNode.has("eTag")) {
            // fallback: tenta inferir de s3.object (alguns payloads do LocalStack não enviam mime)
            mimeType = null;
        }
        
        confirmMessageAttachmentUseCase.execute(messageId, objectKey, fileSize, mimeType);
    }
    
    private UUID extractMessageIdFromKey(String objectKey) {
        String[] parts = objectKey.split("/");
        return UUID.fromString(parts[4]);
    }
}