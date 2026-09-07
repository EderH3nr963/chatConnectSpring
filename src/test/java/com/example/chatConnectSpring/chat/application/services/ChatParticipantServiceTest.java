package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatTypeOperationException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.application.services.ChatParticipantService;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatParticipantServiceTest {
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    
    @Mock
    private ChatNotificationPort chatNotificationPort;
    
    @Mock
    private ChatRepository chatRepository;
    
    @InjectMocks
    private ChatParticipantService chatParticipantService;
    
    @Captor
    private ArgumentCaptor<List<ChatParticipant>> participantsCaptor;
    
    private UUID adminUserId;
    private UUID otherParticipantNotAdminUserId;
    private UUID otherParticipantAdminUserId;
    private UUID chatId;
    private Chat chat;
    private ChatParticipant adminParticipant;
    private ChatParticipant otherParticipantNotAdmin;
    private ChatParticipant otherParticipantAdmin;
    
    @BeforeEach
    void setUp() {
        adminUserId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        otherParticipantNotAdminUserId = UUID.randomUUID();
        otherParticipantAdminUserId = UUID.randomUUID();
        
        chat = new Chat();
        chat.setId(chatId);
        chat.setChatType(ChatTypeEnum.GROUP);
        
        adminParticipant = new ChatParticipant();
        adminParticipant.setId(UUID.randomUUID());
        adminParticipant.setUserId(adminUserId);
        adminParticipant.setChatId(chatId);
        adminParticipant.setRole(ChatParticipantRole.ADMIN);
        
        otherParticipantNotAdmin = new ChatParticipant();
        otherParticipantNotAdmin.setId(UUID.randomUUID()); // CORRIGIDO
        otherParticipantNotAdmin.setUserId(otherParticipantNotAdminUserId);
        otherParticipantNotAdmin.setChatId(chatId);
        otherParticipantNotAdmin.setRole(ChatParticipantRole.DEFAULT);
        
        otherParticipantAdmin = new ChatParticipant(); // CORRIGIDO
        otherParticipantAdmin.setId(UUID.randomUUID()); // CORRIGIDO
        otherParticipantAdmin.setUserId(otherParticipantAdminUserId);
        otherParticipantAdmin.setChatId(chatId);
        otherParticipantAdmin.setRole(ChatParticipantRole.ADMIN);
    }
    
    @Nested
    @DisplayName("Cenários do Método add()")
    class AddMethodTests {
        
        @Test
        @DisplayName("Deve adicionar participantes e enviar notificações com sucesso quando o solicitante for ADMIN")
        void shouldAddParticipantsSuccessfullyWhenUserIsAdmin() {
            UUID newUser1 = UUID.randomUUID();
            UUID newUser2 = UUID.randomUUID();
            AddParticipantsCommand command = new AddParticipantsCommand(List.of(newUser1, newUser2), chatId);
            
            Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId)).thenReturn(adminParticipant);
            
            assertDoesNotThrow(() -> chatParticipantService.add(adminUserId, command));
            
            Mockito.verify(chatParticipantRepository, Mockito.times(1)).saveAll(participantsCaptor.capture());
            List<ChatParticipant> savedParticipants = participantsCaptor.getValue();
            
            assertEquals(2, savedParticipants.size());
            assertTrue(savedParticipants.stream().allMatch(p -> p.getRole() == ChatParticipantRole.DEFAULT));
            assertTrue(savedParticipants.stream().allMatch(p -> p.getChatId().equals(chatId)));
            
            Mockito.verify(chatNotificationPort, Mockito.times(1)).notifyJoinedChat(newUser1, chat);
            Mockito.verify(chatNotificationPort, Mockito.times(1)).notifyJoinedChat(newUser2, chat);
        }
        
        @Test
        @DisplayName("Deve lançar ChatNotFoundException quando o chat não existir")
        void shouldThrowChatNotFoundExceptionWhenChatDoesNotExist() {
            AddParticipantsCommand command = new AddParticipantsCommand(List.of(UUID.randomUUID()), chatId);
            Mockito.when(chatRepository.findById(chatId)).thenReturn(null);
            
            ChatNotFoundException exception = assertThrows(
                    ChatNotFoundException.class,
                    () -> chatParticipantService.add(adminUserId, command)
            );
            
            assertEquals("Chat not found", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).saveAll(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyJoinedChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantNotFoundException quando o usuário solicitante não pertencer ao chat")
        void shouldThrowParticipantNotFoundExceptionWhenRequesterIsNotInChat() {
            AddParticipantsCommand command = new AddParticipantsCommand(List.of(UUID.randomUUID()), chatId);
            Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId)).thenReturn(null);
            
            ParticipantNotFoundException exception = assertThrows(
                    ParticipantNotFoundException.class,
                    () -> chatParticipantService.add(adminUserId, command)
            );
            
            assertEquals("Participant not found", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).saveAll(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyJoinedChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantAccessDeniedException quando o solicitante não for ADMIN")
        void shouldThrowParticipantAccessDeniedExceptionWhenUserIsNotAdmin() {
            adminParticipant.setRole(ChatParticipantRole.DEFAULT);
            
            AddParticipantsCommand command = new AddParticipantsCommand(List.of(UUID.randomUUID()), chatId);
            Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId)).thenReturn(adminParticipant);
            
            ParticipantAccessDeniedException exception = assertThrows(
                    ParticipantAccessDeniedException.class,
                    () -> chatParticipantService.add(adminUserId, command)
            );
            
            assertEquals("You are not allowed to add participants to this chat", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).saveAll(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyJoinedChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar InvalidChatTypeOperationException ao tentar adicionar participantes em um chat PRIVADO")
        void shouldThrowInvalidChatTypeOperationExceptionWhenAddingParticipantToPrivateChat() {
            chat.setChatType(ChatTypeEnum.PRIVATE);
            AddParticipantsCommand command = new AddParticipantsCommand(List.of(UUID.randomUUID()), chatId);
            
            Mockito.when(chatRepository.findById(chatId)).thenReturn(chat);
            
            InvalidChatTypeOperationException exception = assertThrows(
                    InvalidChatTypeOperationException.class,
                    () -> chatParticipantService.add(adminUserId, command)
            );
            
            assertEquals("Cannot add participants to a private chat", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).saveAll(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyJoinedChat(Mockito.any(), Mockito.any());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método remove()")
    class RemoveMethodTests {
        
        @Test
        @DisplayName("Deve remover participante com sucesso quando o solicitante for ADMIN")
        void shouldRemoveParticipantsSuccessfullyWhenUserIsAdmin() {
            UUID participantToBeRemovedId = otherParticipantNotAdmin.getId();
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId))
                    .thenReturn(adminParticipant);
            Mockito.when(chatParticipantRepository.findById(participantToBeRemovedId))
                    .thenReturn(otherParticipantNotAdmin);
            
            chatParticipantService.remove(adminUserId, participantToBeRemovedId, chatId);
            
            Mockito.verify(chatParticipantRepository, Mockito.times(1)).deleteById(participantToBeRemovedId);
            Mockito.verify(chatNotificationPort, Mockito.times(1)).notifyLeftChat(adminUserId, chatId);
        }
        
        @Test
        @DisplayName("Deve lançar ChatNotFoundException ao tentar remover usuário de um chat onde o solicitante não pertence")
        void shouldThrowChatNotFoundException() {
            UUID participantToBeRemovedId = otherParticipantNotAdmin.getId();
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId))
                    .thenReturn(null);
            
            ChatNotFoundException exception = assertThrows(
                    ChatNotFoundException.class,
                    () -> chatParticipantService.remove(adminUserId, participantToBeRemovedId, chatId)
            );
            
            assertEquals("Chat not found", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).deleteById(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyLeftChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantAccessDeniedException quando o usuário tentar remover a si mesmo")
        void shouldThrowParticipantAccessDeniedExceptionWhenTryRemoveYourSelf() {
            UUID myParticipantId = adminParticipant.getId();
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId))
                    .thenReturn(adminParticipant);
            
            ParticipantAccessDeniedException exception = assertThrows(
                    ParticipantAccessDeniedException.class,
                    () -> chatParticipantService.remove(adminUserId, myParticipantId, chatId)
            );
            
            assertEquals("You don't can remove your self", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).deleteById(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyLeftChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantAccessDeniedException quando o solicitante não for ADMIN")
        void shouldThrowParticipantAccessDeniedExceptionWhenNotIsAdmin() {
            UUID myParticipantId = adminParticipant.getId();
            adminParticipant.setRole(ChatParticipantRole.DEFAULT);
            
            UUID participantToBeRemovedId = otherParticipantNotAdmin.getId();
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(myParticipantId, chatId))
                    .thenReturn(adminParticipant);
            
            ParticipantAccessDeniedException exception = assertThrows(
                    ParticipantAccessDeniedException.class,
                    () -> chatParticipantService.remove(myParticipantId, participantToBeRemovedId, chatId)
            );
            
            assertEquals("Only ADMIN can remove participants", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).deleteById(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyLeftChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantNotFoundException quando o participante a ser removido não for encontrado")
        void shouldThrowParticipantNotFoundException() {
            UUID nonExistingParticipantId = UUID.randomUUID();
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId))
                    .thenReturn(adminParticipant);
            Mockito.when(chatParticipantRepository.findById(nonExistingParticipantId))
                    .thenReturn(null);
            
            ParticipantNotFoundException exception = assertThrows(
                    ParticipantNotFoundException.class,
                    () -> chatParticipantService.remove(adminUserId, nonExistingParticipantId, chatId)
            );
            
            assertEquals("Participant to be removed not found", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).deleteById(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyLeftChat(Mockito.any(), Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantAccessDeniedException quando tentar remover outro ADMIN")
        void shouldThrowParticipantAccessDeniedExceptionWhenTryRemoveAnotherAdmin() {
            UUID anotherAdminId = otherParticipantAdmin.getId();
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(adminUserId, chatId))
                    .thenReturn(adminParticipant);
            Mockito.when(chatParticipantRepository.findById(anotherAdminId))
                    .thenReturn(otherParticipantAdmin);
            
            ParticipantAccessDeniedException exception = assertThrows(
                    ParticipantAccessDeniedException.class,
                    () -> chatParticipantService.remove(adminUserId, anotherAdminId, chatId)
            );
            
            assertEquals("You don't can remove another ADMIN", exception.getMessage());
            Mockito.verify(chatParticipantRepository, Mockito.never()).deleteById(Mockito.any());
            Mockito.verify(chatNotificationPort, Mockito.never()).notifyLeftChat(Mockito.any(), Mockito.any());
        }
    }
}
