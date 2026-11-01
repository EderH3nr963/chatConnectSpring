package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.user.application.commands.GenerateUserAvatarUploadUrlCommand;
import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UserStoragePort userStoragePort;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Deve gerar URL pré-assinada para avatar quando parâmetros forem válidos (PNG)")
    void shouldGeneratePresignedUrlForPngAvatar() {
        when(usuarioRepository.findById(userId)).thenReturn(user);
        String expectedObjectKey = "uploads/user/avatar/" + userId + ".png";
        String expectedUrl = "https://s3.amazonaws.com/bucket/" + expectedObjectKey + "?token=xyz";

        when(userStoragePort.generatePresignedUploadUrl(eq(expectedObjectKey), eq("image/png"), any(Duration.class)))
                .thenReturn(expectedUrl);

        GenerateUserAvatarUploadUrlCommand command = new GenerateUserAvatarUploadUrlCommand("image/png");
        String result = userService.generateAvatarUploadUrl(userId, command);

        assertEquals(expectedUrl, result);
        verify(userStoragePort).generatePresignedUploadUrl(expectedObjectKey, "image/png", Duration.ofMinutes(10));
    }

    @Test
    @DisplayName("Deve gerar URL pré-assinada para avatar quando parâmetros forem válidos (JPEG)")
    void shouldGeneratePresignedUrlForJpegAvatar() {
        when(usuarioRepository.findById(userId)).thenReturn(user);
        String expectedObjectKey = "uploads/user/avatar/" + userId + ".jpg";
        String expectedUrl = "https://s3.amazonaws.com/bucket/" + expectedObjectKey + "?token=xyz";

        when(userStoragePort.generatePresignedUploadUrl(eq(expectedObjectKey), eq("image/jpeg"), any(Duration.class)))
                .thenReturn(expectedUrl);

        GenerateUserAvatarUploadUrlCommand command = new GenerateUserAvatarUploadUrlCommand("image/jpeg");
        String result = userService.generateAvatarUploadUrl(userId, command);

        assertEquals(expectedUrl, result);
        verify(userStoragePort).generatePresignedUploadUrl(expectedObjectKey, "image/jpeg", Duration.ofMinutes(10));
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário não for encontrado")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        when(usuarioRepository.findById(userId)).thenReturn(null);

        GenerateUserAvatarUploadUrlCommand command = new GenerateUserAvatarUploadUrlCommand("image/png");

        assertThrows(UserNotFoundException.class, () -> userService.generateAvatarUploadUrl(userId, command));
        verify(userStoragePort, never()).generatePresignedUploadUrl(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException para content type de imagem não suportado")
    void shouldThrowIllegalArgumentExceptionForUnsupportedContentType() {
        when(usuarioRepository.findById(userId)).thenReturn(user);

        GenerateUserAvatarUploadUrlCommand command = new GenerateUserAvatarUploadUrlCommand("application/pdf");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.generateAvatarUploadUrl(userId, command)
        );

        assertEquals("Formato de imagem não suportado", exception.getMessage());
        verify(userStoragePort, never()).generatePresignedUploadUrl(any(), any(), any());
    }
}
