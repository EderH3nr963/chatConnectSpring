package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidMessageException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.Message;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageNotificationPort;
import com.example.chatConnectSpring.chat.domain.ports.out.MessageRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.PresenceStateOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    
    @Mock
    private MessageRepository messageRepository;
    
    @Mock
    private MessageNotificationPort messageNotificationPort;
    
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    
    @Mock
    private PresenceStateOutputPort presenceStateOutputPort;
    
    @InjectMocks
    private MessageService messageService;
    
    @Captor
    private ArgumentCaptor<Message> messageCaptor;
    
    private UUID userId;
    private UUID chatId;
    private UUID messageId;
    private ChatParticipant authorParticipant;
    private Message message;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        messageId = UUID.randomUUID();
        
        authorParticipant = new ChatParticipant();
        authorParticipant.setId(UUID.randomUUID());
        authorParticipant.setUserId(userId);
        authorParticipant.setChatId(chatId);
        authorParticipant.setRole(ChatParticipantRole.DEFAULT);
        authorParticipant.setUnreadMessages(0);
        
        message = new Message();
        message.setId(messageId);
        message.setChatId(chatId);
        message.setSenderId(authorParticipant.getId());
        message.setContent("Mensagem original");
    }
    
    @Nested()
    @DisplayName("Cenários do Método send()")
    class SendMessageTests {
        @Test
        @DisplayName("Deve enviar mensagem com sucesso e notificar apenas participantes inativos")
        void send_ShouldSendMessageAndNotifyInactiveParticipants_WhenDataIsValid() {
            SendMessageCommand command = new SendMessageCommand(chatId, "  Olá, mundo!  ");
            
            ChatParticipant inactiveParticipant = new ChatParticipant();
            UUID inactiveUserId = UUID.randomUUID();
            inactiveParticipant.setId(UUID.randomUUID());
            inactiveParticipant.setUserId(inactiveUserId);
            inactiveParticipant.setChatId(chatId);
            inactiveParticipant.setUnreadMessages(1);
            
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message msgToSave = invocation.getArgument(0);
                msgToSave.setId(messageId);
                return msgToSave;
            });
            
            when(chatParticipantRepository.findByChatId(chatId))
                    .thenReturn(List.of(authorParticipant, inactiveParticipant));
            
            when(presenceStateOutputPort.usersInChat(chatId.toString()))
                    .thenReturn(Set.of(userId.toString()));
            
            Message result = messageService.send(userId, command);
            
            assertNotNull(result);
            assertEquals(messageId, result.getId());
            assertEquals("Olá, mundo!", result.getContent()); // Garante o trim()
            assertEquals(authorParticipant.getId(), result.getSenderId());
            assertEquals(chatId, result.getChatId());
            
            // Valida se o contador de não lidas do participante inativo aumentou (1 -> 2)
            assertEquals(2, inactiveParticipant.getUnreadMessages());
            // Valida que o participante ativo não teve o contador incrementado (0)
            assertEquals(0, authorParticipant.getUnreadMessages());
            
            // Verifica envio das notificações
            verify(messageNotificationPort, times(1))
                    .notifyMessageSent(result, List.of(inactiveParticipant));
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Deve lançar InvalidMessageException quando o conteúdo for nulo ou em branco")
        void send_ShouldThrowInvalidMessageException_WhenContentIsInvalid(String invalidContent) {
            SendMessageCommand command = new SendMessageCommand(chatId, invalidContent);
            
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.send(userId, command)
            );
            
            assertEquals("Message content cannot be empty.", exception.getMessage());
            verifyNoInteractions(chatParticipantRepository, messageRepository, presenceStateOutputPort, messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar InvalidMessageException quando o comando for nulo")
        void send_ShouldThrowInvalidMessageException_WhenCommandIsNull() {
            // Act & Assert
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.send(userId, null)
            );
            
            assertEquals("Message content cannot be empty.", exception.getMessage());
            verifyNoInteractions(chatParticipantRepository, messageRepository, presenceStateOutputPort, messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o usuário não for participante do chat")
        void send_ShouldThrowMessageAccessDeniedException_WhenUserIsNotParticipant() {
            SendMessageCommand command = new SendMessageCommand(chatId, "Mensagem válida");
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(null);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.send(userId, command)
            );
            
            assertEquals("User is not a participant of this chat.", exception.getMessage());
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verifyNoInteractions(messageRepository, presenceStateOutputPort, messageNotificationPort);
        }
    }
    
    @Nested
    @DisplayName("Testes do método edit")
    class EditMessageTests {
        
        @Test
        @DisplayName("Deve editar a mensagem com sucesso quando o usuário for o autor")
        void edit_ShouldEditMessage_WhenUserIsAuthorAndDataIsValid() {
            EditMessageCommand command = new EditMessageCommand(messageId, chatId, " Mensagem editada ");
            
            when(messageRepository.findById(messageId)).thenReturn(message);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.update(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            Message result = messageService.edit(userId, command);
            
            assertNotNull(result);
            assertEquals("Mensagem editada", result.getContent());
            assertEquals(messageId, result.getId());
            
            verify(messageRepository, times(1)).update(message);
            verify(messageNotificationPort, times(1)).notifyMessageEdited(result);
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Deve lançar InvalidMessageException quando o conteúdo for nulo ou em branco")
        void edit_ShouldThrowInvalidMessageException_WhenContentIsInvalid(String invalidContent) {
            EditMessageCommand command = new EditMessageCommand(messageId, chatId, invalidContent);
            
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.edit(userId, command)
            );
            
            assertEquals("Message content cannot be empty.", exception.getMessage());
            verifyNoInteractions(messageRepository, chatParticipantRepository, messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar InvalidMessageException quando o comando for nulo")
        void edit_ShouldThrowInvalidMessageException_WhenCommandIsNull() {
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.edit(userId, null)
            );
            
            assertEquals("Message content cannot be empty.", exception.getMessage());
            verifyNoInteractions(messageRepository, chatParticipantRepository, messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar MessageNotFoundException quando a mensagem não for encontrada")
        void edit_ShouldThrowMessageNotFoundException_WhenMessageDoesNotExist() {
            EditMessageCommand command = new EditMessageCommand(messageId, chatId, "Novo texto");
            when(messageRepository.findById(messageId)).thenReturn(null);
            
            MessageNotFoundException exception = assertThrows(
                    MessageNotFoundException.class,
                    () -> messageService.edit(userId, command)
            );
            
            assertEquals("Message not found: " + messageId, exception.getMessage());
            verify(messageRepository, times(1)).findById(messageId);
            verifyNoMoreInteractions(messageRepository);
            verifyNoInteractions(chatParticipantRepository, messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o participante não for encontrado")
        void edit_ShouldThrowMessageAccessDeniedException_WhenParticipantNotFound() {
            UUID anotherChatId = UUID.randomUUID();
            
            EditMessageCommand command = new EditMessageCommand(messageId, anotherChatId, "Novo texto");
            when(messageRepository.findById(messageId)).thenReturn(message);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, anotherChatId)).thenReturn(null);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.edit(userId, command)
            );
            
            assertEquals("Only the author can edit this message.", exception.getMessage());
            verify(messageRepository, never()).update(any());
            verifyNoInteractions(messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o usuário não for o autor da mensagem")
        void edit_ShouldThrowMessageAccessDeniedException_WhenUserIsNotAuthor() {
            EditMessageCommand command = new EditMessageCommand(messageId, chatId, "Novo texto");
            
            ChatParticipant anotherParticipant = new ChatParticipant();
            anotherParticipant.setId(UUID.randomUUID());
            anotherParticipant.setUserId(userId);
            anotherParticipant.setChatId(chatId);
            
            when(messageRepository.findById(messageId)).thenReturn(message);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(anotherParticipant);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.edit(userId, command)
            );
            
            assertEquals("Only the author can edit this message.", exception.getMessage());
            verify(messageRepository, never()).update(any());
            verifyNoInteractions(messageNotificationPort);
        }
    }
    
    @Nested
    @DisplayName("Testes do método delete")
    class DeleteMessageTests {
        
        @Test
        @DisplayName("Deve deletar mensagem com sucesso quando o usuário for o autor E administrador do chat")
        void delete_ShouldDeleteMessage_WhenUserIsAuthorAndAdmin() {
            authorParticipant.setRole(ChatParticipantRole.ADMIN);
            
            when(messageRepository.findById(messageId)).thenReturn(message);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            
            messageService.delete(userId, messageId);
            
            verify(messageRepository, times(1)).deleteById(messageId);
            verify(messageNotificationPort, times(1)).notifyMessageDeleted(chatId, messageId);
        }
        
        @Test
        @DisplayName("Deve lançar MessageNotFoundException quando a mensagem não for encontrada")
        void delete_ShouldThrowMessageNotFoundException_WhenMessageDoesNotExist() {
            when(messageRepository.findById(messageId)).thenReturn(null);
            
            MessageNotFoundException exception = assertThrows(
                    MessageNotFoundException.class,
                    () -> messageService.delete(userId, messageId)
            );
            
            assertEquals("Message not found: " + messageId, exception.getMessage());
            verify(chatParticipantRepository, never()).findByUserIdAndChatId(any(), any());
            verify(messageRepository, never()).deleteById(any());
            verifyNoInteractions(messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o usuário não for participante do chat")
        void delete_ShouldThrowMessageAccessDeniedException_WhenUserIsNotParticipant() {
            when(messageRepository.findById(messageId)).thenReturn(message);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(null);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.delete(userId, messageId)
            );
            
            assertEquals("User is not a participant of this chat.", exception.getMessage());
            verify(messageRepository, never()).deleteById(any());
            verifyNoInteractions(messageNotificationPort);
        }
    }
    
    @Nested
    @DisplayName("Testes do método findMessagesByChatId")
    class FindMessagesByChatIdTests {
        
        @Test
        @DisplayName("Deve retornar a lista de mensagens quando o usuário for participante do chat")
        void findMessagesByChatId_ShouldReturnMessages_WhenUserIsParticipant() {
            List<Message> expectedMessages = List.of(message);
            
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.findByChatId(chatId)).thenReturn(expectedMessages);
            
            List<Message> result = messageService.findMessagesByChatId(userId, chatId);
            
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(messageId, result.get(0).getId());
            assertEquals("Mensagem original", result.get(0).getContent());
            
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verify(messageRepository, times(1)).findByChatId(chatId);
        }
        
        @Test
        @DisplayName("Deve retornar lista vazia quando não houver mensagens no chat")
        void findMessagesByChatId_ShouldReturnEmptyList_WhenChatHasNoMessages() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.findByChatId(chatId)).thenReturn(Collections.emptyList());
            
            List<Message> result = messageService.findMessagesByChatId(userId, chatId);
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
            
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verify(messageRepository, times(1)).findByChatId(chatId);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o usuário não for participante do chat")
        void findMessagesByChatId_ShouldThrowMessageAccessDeniedException_WhenUserIsNotParticipant() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(null);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.findMessagesByChatId(userId, chatId)
            );
            
            assertEquals("User is not a participant of this chat.", exception.getMessage());
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verifyNoInteractions(messageRepository);
        }
    }
}