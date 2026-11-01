package com.example.chatConnectSpring.user.infrastructure.adapters.in.http;

import com.example.chatConnectSpring.auth.infrastructure.security.UserDetailsImpl;
import com.example.chatConnectSpring.user.application.commands.GenerateUserAvatarUploadUrlCommand;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserUseCase;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.request.GenerateUserAvatarUploadUrlRequest;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.PresignedUrlResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserUseCase userUseCase;

    @InjectMocks
    private UserController userController;

    private UUID userId;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = new User(userId, "test@example.com", "testuser", "clerk_123", OffsetDateTime.now(), OffsetDateTime.now());
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    @DisplayName("Deve retornar status 200 e a URL pré-assinada gerada")
    void shouldReturnOkWithPresignedUrl() {
        String expectedUploadUrl = "https://s3.amazonaws.com/chat-connect-app/uploads/user/avatar/test.png";
        GenerateUserAvatarUploadUrlRequest request = new GenerateUserAvatarUploadUrlRequest("image/png");

        when(userUseCase.generateAvatarUploadUrl(eq(userId), eq(new GenerateUserAvatarUploadUrlCommand("image/png"))))
                .thenReturn(expectedUploadUrl);

        ResponseEntity<PresignedUrlResponse> response = userController.generateAvatarUploadUrl(userDetails, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedUploadUrl, response.getBody().uploadUrl());

        verify(userUseCase).generateAvatarUploadUrl(eq(userId), eq(new GenerateUserAvatarUploadUrlCommand("image/png")));
    }
}
