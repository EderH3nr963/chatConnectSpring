package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAvatarUserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UpdateAvatarUserService updateAvatarUserService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
    }

    @Test
    @DisplayName("Deve atualizar o avatarKey do usuário com sucesso")
    void shouldUpdateUserAvatarSuccessfully() {
        String objectKey = "uploads/user/avatar/" + userId + ".png";
        when(usuarioRepository.findById(userId)).thenReturn(user);

        updateAvatarUserService.execute(userId, objectKey);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(usuarioRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals(objectKey, savedUser.getAvatarKey());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando o usuário não existir")
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        String objectKey = "uploads/user/avatar/" + userId + ".png";
        when(usuarioRepository.findById(userId)).thenReturn(null);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> updateAvatarUserService.execute(userId, objectKey)
        );

        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(usuarioRepository, never()).save(any());
    }
}
