package com.example.chatConnectSpring.friendship.application.services;

import com.example.chatConnectSpring.friendship.application.exception.InvalidFriendshipException;
import com.example.chatConnectSpring.friendship.application.exception.UserIsNotBlockedException;
import com.example.chatConnectSpring.friendship.domain.model.userblock.UserBlock;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendShipRepository;
import com.example.chatConnectSpring.friendship.domain.ports.out.UserBlockRepository;
import com.example.chatConnectSpring.user.application.service.UserService;
import com.example.chatConnectSpring.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserBlockServiceTest {
    
    @Mock
    private FriendShipRepository friendShipRepository;

    @Mock
    private org.springframework.context.ApplicationEventPublisher publisher;
    
    @Mock
    private UserService userService;
    
    @Mock
    private UserBlockRepository userBlockRepository;
    
    @InjectMocks
    private UserBlockService userBlockService;
    
    private final UUID userId = UUID.randomUUID();
    private final UUID blockedUserId = UUID.randomUUID();
    
    @Nested
    @DisplayName("Cenários do Método blockUser()")
    class BlockUser {
        
        @Test
        @DisplayName("Deve criar nova relação de bloqueio e remover amizade prévia quando usuários forem válidos")
        void shouldCreateBlockRelationWhenUsersAreValid() {
            User user = new User();
            User targetUser = new User();
            
            when(userService.findById(userId)).thenReturn(user);
            when(userService.findById(blockedUserId)).thenReturn(targetUser);
            when(userBlockRepository.existsByBlockerIdAndBlockedId(userId, blockedUserId)).thenReturn(false);
            
            assertDoesNotThrow(() -> userBlockService.blockUser(userId, blockedUserId));
            
            verify(userBlockRepository, times(1)).save(any(UserBlock.class));
        }
        
        @Test
        @DisplayName("Deve lançar InvalidFriendshipException se o usuário a ser bloqueado já estiver bloqueado")
        void shouldThrowWhenUserIsAlreadyBlocked() {
            User user = new User();
            User targetUser = new User();
            
            when(userService.findById(userId)).thenReturn(user);
            when(userService.findById(blockedUserId)).thenReturn(targetUser);
            when(userBlockRepository.existsByBlockerIdAndBlockedId(userId, blockedUserId)).thenReturn(true);
            
            InvalidFriendshipException exception = assertThrows(
                    InvalidFriendshipException.class,
                    () -> userBlockService.blockUser(userId, blockedUserId)
            );
            
            assertEquals("User is already blocked.", exception.getMessage());
            verify(userBlockRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Deve lançar InvalidFriendshipException ao tentar bloquear a si mesmo")
        void shouldThrowWhenBlockingSelf() {
            InvalidFriendshipException exception = assertThrows(
                    InvalidFriendshipException.class,
                    () -> userBlockService.blockUser(userId, userId)
            );
            
            assertEquals("Operation cannot be performed with yourself.", exception.getMessage());
            verify(userService, never()).findById(any());
        }
        
        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando o ID do usuário ou do alvo for nulo")
        void shouldThrowWhenUserIdsAreNullOnBlock() {
            assertThrows(IllegalArgumentException.class, () -> userBlockService.blockUser(null, blockedUserId));
            assertThrows(IllegalArgumentException.class, () -> userBlockService.blockUser(userId, null));
            verify(userBlockRepository, never()).save(any());
        }
        
        @Test
        @DisplayName("Deve lançar InvalidFriendshipException quando um dos usuários não existir")
        void shouldThrowWhenUserDoesNotExistOnBlock() {
            when(userService.findById(userId)).thenReturn(null);
            
            InvalidFriendshipException exception = assertThrows(
                    InvalidFriendshipException.class,
                    () -> userBlockService.blockUser(userId, blockedUserId)
            );
            
            assertTrue(exception.getMessage().contains("User does not exist"));
            verify(userBlockRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método unblockUser()")
    class UnblockUser {
        
        @Test
        @DisplayName("Deve desbloquear usuário com sucesso quando a relação de bloqueio existir")
        void shouldUnblockSuccessfullyWhenBlocked() {
            when(userBlockRepository.existsByBlockerIdAndBlockedId(userId, blockedUserId)).thenReturn(true);
            
            assertDoesNotThrow(() -> userBlockService.unblockUser(userId, blockedUserId));
            
            verify(userBlockRepository, times(1)).deleteByBlockerAndBlockedUserId(userId, blockedUserId);
        }
        
        @Test
        @DisplayName("Deve lançar UserIsNotBlockedException quando tentar desbloquear um usuário que não está bloqueado")
        void shouldThrowWhenUserIsNotBlocked() {
            when(userBlockRepository.existsByBlockerIdAndBlockedId(userId, blockedUserId)).thenReturn(false);
            
            UserIsNotBlockedException exception = assertThrows(
                    UserIsNotBlockedException.class,
                    () -> userBlockService.unblockUser(userId, blockedUserId)
            );
            
            assertEquals("User is not blocked.", exception.getMessage());
            verify(userBlockRepository, never()).deleteByBlockerAndBlockedUserId(any(), any());
        }
        
        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando o ID do usuário ou do alvo for nulo")
        void shouldThrowWhenUserIdsAreNullOnUnblock() {
            assertThrows(IllegalArgumentException.class, () -> userBlockService.unblockUser(null, blockedUserId));
            assertThrows(IllegalArgumentException.class, () -> userBlockService.unblockUser(userId, null));
            verify(userBlockRepository, never()).deleteByBlockerAndBlockedUserId(any(), any());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método listBlockedUsers()")
    class ListBlockedUsers {
        
        @Test
        @DisplayName("Deve retornar a lista de usuários bloqueados para um ID válido")
        void shouldReturnListOfBlockedUsersForValidUserId() {
            UserBlock block = new UserBlock();
            block.setBlockerId(userId);
            block.setBlockedId(blockedUserId);
            
            when(userBlockRepository.findBlockedBy(userId)).thenReturn(List.of(block));
            
            List<UserBlock> result = userBlockService.listBlockedUsers(userId);
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(blockedUserId, result.get(0).getBlockedId());
            verify(userBlockRepository, times(1)).findBlockedBy(userId);
        }
        
        @Test
        @DisplayName("Deve lançar InvalidFriendshipException quando o ID do usuário for nulo")
        void shouldThrowWhenUserIdIsNullOnListBlocked() {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userBlockService.listBlockedUsers(null)
            );
            
            assertEquals("User ID cannot be null.", exception.getMessage());
            verify(userBlockRepository, never()).findBlockedBy(any());
        }
    }
}