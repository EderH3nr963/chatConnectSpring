package com.example.chatConnectSpring.user.infrastructure.adapters.in.messaging;

import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.port.in.UpdateAvatarUserUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3UserAvatarEventListenerTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UpdateAvatarUserUseCase updateAvatarUserUseCase;

    @InjectMocks
    private S3UserAvatarEventListener listener;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    private String buildS3Payload(String eventSource, String eventName, String key) {
        return """
            {
              "Records": [
                {
                  "eventSource": "%s",
                  "eventName": "%s",
                  "s3": {
                    "bucket": { "name": "chat-connect-app" },
                    "object": {
                      "key": "%s",
                      "size": 1024
                    }
                  }
                }
              ]
            }
            """.formatted(eventSource, eventName, key);
    }

    @Test
    @DisplayName("Deve processar evento com sucesso quando o upload de avatar for válido")
    void shouldProcessValidUserAvatarUploadEvent() throws JsonProcessingException {
        String key = "uploads/user/avatar/" + userId + ".png";
        String payload = buildS3Payload("aws:s3", "ObjectCreated:Put", key);

        listener.handleUserAvatarUploaded(payload);

        verify(updateAvatarUserUseCase, times(1)).execute(userId, key);
    }

    @Test
    @DisplayName("Deve processar evento com sucesso para chave com uploads/users/avatar/")
    void shouldProcessValidUserAvatarWithPluralPrefix() throws JsonProcessingException {
        String key = "uploads/users/avatar/" + userId + ".jpg";
        String payload = buildS3Payload("aws:s3", "ObjectCreated:Put", key);

        listener.handleUserAvatarUploaded(payload);

        verify(updateAvatarUserUseCase, times(1)).execute(userId, key);
    }

    @Test
    @DisplayName("Deve processar evento com sucesso mesmo se a chave começar com barra")
    void shouldProcessValidUserAvatarWithLeadingSlash() throws JsonProcessingException {
        String key = "/uploads/user/avatar/" + userId + ".png";
        String payload = buildS3Payload("aws:s3", "ObjectCreated:Put", key);

        listener.handleUserAvatarUploaded(payload);

        verify(updateAvatarUserUseCase, times(1)).execute(userId, key);
    }

    @Test
    @DisplayName("Deve ignorar evento quando eventSource não for aws:s3")
    void shouldIgnoreWhenEventSourceIsNotAwsS3() throws JsonProcessingException {
        String key = "uploads/user/avatar/" + userId + ".png";
        String payload = buildS3Payload("aws:sqs", "ObjectCreated:Put", key);

        listener.handleUserAvatarUploaded(payload);

        verify(updateAvatarUserUseCase, never()).execute(any(), any());
    }

    @Test
    @DisplayName("Deve ignorar evento quando eventName não for ObjectCreated")
    void shouldIgnoreWhenEventNameIsNotObjectCreated() throws JsonProcessingException {
        String key = "uploads/user/avatar/" + userId + ".png";
        String payload = buildS3Payload("aws:s3", "ObjectRemoved:Delete", key);

        listener.handleUserAvatarUploaded(payload);

        verify(updateAvatarUserUseCase, never()).execute(any(), any());
    }

    @Test
    @DisplayName("Deve ignorar chave que não corresponder ao prefixo de avatar de usuário")
    void shouldIgnoreKeyWithUnmatchedPrefix() throws JsonProcessingException {
        String key = "uploads/chat/avatar/some-chat-id.png";
        String payload = buildS3Payload("aws:s3", "ObjectCreated:Put", key);

        listener.handleUserAvatarUploaded(payload);

        verify(updateAvatarUserUseCase, never()).execute(any(), any());
    }

    @Test
    @DisplayName("Deve ignorar payload sem Records ou vazio")
    void shouldIgnoreEmptyOrMissingRecords() throws JsonProcessingException {
        String payload1 = "{\"Records\": []}";
        String payload2 = "{}";

        listener.handleUserAvatarUploaded(payload1);
        listener.handleUserAvatarUploaded(payload2);

        verify(updateAvatarUserUseCase, never()).execute(any(), any());
    }

    @Test
    @DisplayName("Deve tratar UserNotFoundException sem propagar exceção")
    void shouldHandleUserNotFoundExceptionGracefully() throws JsonProcessingException {
        String key = "uploads/user/avatar/" + userId + ".png";
        String payload = buildS3Payload("aws:s3", "ObjectCreated:Put", key);

        doThrow(new UserNotFoundException("User not found")).when(updateAvatarUserUseCase).execute(userId, key);

        assertDoesNotThrow(() -> listener.handleUserAvatarUploaded(payload));
        verify(updateAvatarUserUseCase, times(1)).execute(userId, key);
    }

    @Test
    @DisplayName("Testes de extração do UUID a partir da chave S3")
    void testExtractUserIdFromKey() {
        assertEquals(userId, listener.extractUserIdFromKey("uploads/user/avatar/" + userId + ".png"));
        assertEquals(userId, listener.extractUserIdFromKey("/uploads/user/avatar/" + userId + ".jpg"));
        assertEquals(userId, listener.extractUserIdFromKey("uploads/users/avatar/" + userId + ".png"));
        assertEquals(userId, listener.extractUserIdFromKey("uploads/user/avatar/" + userId));

        assertThrows(IllegalArgumentException.class, () -> listener.extractUserIdFromKey("unsupported/key/" + userId));
        assertThrows(IllegalArgumentException.class, () -> listener.extractUserIdFromKey(null));
    }

    @Test
    @DisplayName("Testes do método supports")
    void testSupports() {
        assertTrue(listener.supports("uploads/user/avatar/" + userId + ".png"));
        assertTrue(listener.supports("/uploads/user/avatar/" + userId + ".png"));
        assertTrue(listener.supports("uploads/users/avatar/" + userId + ".png"));
        assertFalse(listener.supports("uploads/chat/avatar/" + userId));
        assertFalse(listener.supports(null));
    }
}
