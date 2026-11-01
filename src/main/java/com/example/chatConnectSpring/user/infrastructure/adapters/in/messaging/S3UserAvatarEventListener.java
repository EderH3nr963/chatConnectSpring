package com.example.chatConnectSpring.user.infrastructure.adapters.in.messaging;

import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.port.in.UpdateAvatarUserUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class S3UserAvatarEventListener {

    private static final Logger log = LoggerFactory.getLogger(S3UserAvatarEventListener.class);

    private static final String USER_AVATAR_PREFIX = "uploads/user/avatar/";

    private final ObjectMapper objectMapper;
    private final UpdateAvatarUserUseCase updateAvatarUserUseCase;

    public S3UserAvatarEventListener(ObjectMapper objectMapper, UpdateAvatarUserUseCase updateAvatarUserUseCase) {
        this.objectMapper = objectMapper;
        this.updateAvatarUserUseCase = updateAvatarUserUseCase;
    }

    @SqsListener("s3-user-avatar-events-queue")
    public void handleUserAvatarUploaded(String messagePayload) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(messagePayload);
        JsonNode records = rootNode.path("Records");

        if (!records.isArray() || records.isEmpty()) {
            log.warn("Payload ignorado: campo 'Records' ausente ou vazio.");
            return;
        }

        for (JsonNode record : records) {
            String eventSource = record.path("eventSource").asText();
            String eventName = record.path("eventName").asText();

            if (!"aws:s3".equals(eventSource) || !eventName.startsWith("ObjectCreated:")) {
                log.debug("Evento ignorado: source/name incompatível -> {} / {}", eventSource, eventName);
                continue;
            }

            String objectKey = record.path("s3").path("object").path("key").asText();

            if (!supports(objectKey)) {
                log.warn("Chave S3 ignorada por não pertencer a avatar de usuário: {}", objectKey);
                continue;
            }

            try {
                UUID userId = extractUserIdFromKey(objectKey);
                updateAvatarUserUseCase.execute(userId, objectKey);
                log.info("Avatar do usuário {} atualizado com sucesso a partir da chave {}", userId, objectKey);
            } catch (UserNotFoundException e) {
                log.error("Usuário não encontrado ao processar upload de avatar: {}", e.getMessage());
            } catch (Exception e) {
                log.error("Erro inesperado ao processar evento de avatar S3 para chave {}: {}", objectKey, e.getMessage(), e);
            }
        }
    }

    public boolean supports(String objectKey) {
        if (objectKey == null) {
            return false;
        }
        String cleanKey = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        return cleanKey.startsWith(USER_AVATAR_PREFIX);
    }

    public UUID extractUserIdFromKey(String objectKey) {
        if (objectKey == null) {
            throw new IllegalArgumentException("Chave S3 não pode ser nula");
        }
        String cleanKey = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
        String filename;
        
        if (!cleanKey.startsWith(USER_AVATAR_PREFIX)) {
            throw new IllegalArgumentException("Chave S3 com prefixo inválido para avatar de usuário: " + objectKey);
        }
        
        filename = cleanKey.substring(USER_AVATAR_PREFIX.length());

        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        String userIdStr = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;
        if (userIdStr.length() >= 36) {
            userIdStr = userIdStr.substring(0, 36);
        }
        return UUID.fromString(userIdStr);
    }
}
