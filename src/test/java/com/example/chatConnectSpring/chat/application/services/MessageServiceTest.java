package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.EditMessageCommand;
import com.example.chatConnectSpring.chat.application.commands.SendMessageCommand;
import com.example.chatConnectSpring.chat.application.events.MessageSentEvent;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidMessageException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.MessageNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.domain.model.message.MessageType;
import com.example.chatConnectSpring.chat.domain.model.message.SendMessageResult;
import com.example.chatConnectSpring.chat.domain.ports.out.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    
    @Mock
    private MessageRepository messageRepository;
    
    @Mock
    private MessageNotificationPort messageNotificationPort;
    
    @Mock
    private EventPublisherPort eventPublisher;

    @Mock
    private ChatResolver chatResolver;

    @Mock
    private StoragePort storagePort;

    @Mock
    private S3CleanupOutboxRepository s3CleanupOutboxRepository;
    
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    
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
        @DisplayName("Deve enviar mensagem com sucesso e publicar evento quando dados são válidos")
        void send_ShouldSendMessageAndPublishEvent_WhenDataIsValid() {
            SendMessageCommand command = new SendMessageCommand(null, chatId, "  Olá, mundo!  ", null, MessageType.TEXT);
            
            when(chatResolver.resolveChatId(userId, command)).thenReturn(chatId);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
                Message msgToSave = invocation.getArgument(0);
                msgToSave.setId(messageId);
                return msgToSave;
            });
            
            SendMessageResult result = messageService.send(userId, command);
            
            assertNotNull(result);
            assertEquals(messageId, result.message().getId());
            assertEquals("Olá, mundo!", result.message().getContent());
            assertEquals(chatId, result.message().getChatId());
            
            verify(chatResolver, times(1)).resolveChatId(userId, command);
            verify(messageRepository, times(1)).save(any(Message.class));
            verify(eventPublisher, times(1))
                    .publishEvent(any(MessageSentEvent.class));
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Deve lançar InvalidMessageException quando o conteúdo for nulo ou em branco")
        void send_ShouldThrowInvalidMessageException_WhenContentIsInvalid(String invalidContent) {
            // SendMessageCommand não valida no construtor — valida no service — então o conteúdo vazio só dá InvalidMessageException
            // quando não tem attachment. Esses casos devem dar InvalidMessageException pelo service.
            SendMessageCommand command = new SendMessageCommand(null, chatId, invalidContent, null, MessageType.TEXT);
            
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.send(userId, command)
            );
            
            assertNotNull(exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve lançar InvalidMessageException quando o comando for nulo")
        void send_ShouldThrowInvalidMessageException_WhenCommandIsNull() {
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.send(userId, null)
            );
            
            assertNotNull(exception.getMessage());
            verifyNoInteractions(chatResolver, chatParticipantRepository, messageRepository, eventPublisher, storagePort);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o usuário não for participante do chat")
        void send_ShouldThrowMessageAccessDeniedException_WhenUserIsNotParticipant() {
            SendMessageCommand command = new SendMessageCommand(null, chatId, "Mensagem válida", null, MessageType.TEXT);
            when(chatResolver.resolveChatId(userId, command)).thenReturn(chatId);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(null);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.send(userId, command)
            );
            
            assertEquals("User is not a participant of this chat.", exception.getMessage());
            verify(chatResolver, times(1)).resolveChatId(userId, command);
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verifyNoInteractions(messageRepository, eventPublisher, storagePort);
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
            // EditMessageCommand valida no construtor (compact constructor), então o InvalidMessageException vem na criação do command
            assertThrows(
                    InvalidMessageException.class,
                    () -> new EditMessageCommand(messageId, chatId, invalidContent)
            );
            verifyNoInteractions(messageRepository, chatParticipantRepository, messageNotificationPort);
        }
        
        @Test
        @DisplayName("Deve lançar InvalidMessageException quando o comando for nulo")
        void edit_ShouldThrowInvalidMessageException_WhenCommandIsNull() {
            InvalidMessageException exception = assertThrows(
                    InvalidMessageException.class,
                    () -> messageService.edit(userId, null)
            );
            
            assertNotNull(exception.getMessage());
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
            verify(s3CleanupOutboxRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve enfileirar outbox quando mensagem tem anexo S3")
        void delete_ShouldEnqueueOutbox_WhenMessageHasAttachment() {
            authorParticipant.setRole(ChatParticipantRole.ADMIN);
            message.setFileS3Key("chat/abc/file.jpg");

            when(messageRepository.findById(messageId)).thenReturn(message);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(s3CleanupOutboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            messageService.delete(userId, messageId);

            verify(messageRepository, times(1)).deleteById(messageId);
            verify(s3CleanupOutboxRepository, times(1)).save(argThat(t -> t.getS3Key().equals("chat/abc/file.jpg") && t.getMessageId().equals(messageId)));
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
    @DisplayName("Testes do método findMessagesByChatId com paginação")
    class FindMessagesByChatIdTests {
        
        @Test
        @DisplayName("Deve retornar Slice de mensagens quando o usuário for participante do chat")
        void findMessagesByChatId_ShouldReturnSlice_WhenUserIsParticipant() {
            Pageable pageable = PageRequest.of(0, 20);
            Slice<Message> expectedSlice = new SliceImpl<>(List.of(message), pageable, false);
            
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.findByChatId(chatId, pageable)).thenReturn(expectedSlice);
            
            Slice<Message> result = messageService.findMessagesByChatId(userId, chatId, pageable);
            
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(messageId, result.getContent().get(0).getId());
            assertEquals("Mensagem original", result.getContent().get(0).getContent());
            
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verify(messageRepository, times(1)).findByChatId(chatId, pageable);
        }
        
        @Test
        @DisplayName("Deve retornar Slice vazio quando não houver mensagens no chat")
        void findMessagesByChatId_ShouldReturnEmptySlice_WhenChatHasNoMessages() {
            Pageable pageable = PageRequest.of(0, 20);
            Slice<Message> emptySlice = new SliceImpl<>(Collections.emptyList(), pageable, false);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.findByChatId(chatId, pageable)).thenReturn(emptySlice);
            
            Slice<Message> result = messageService.findMessagesByChatId(userId, chatId, pageable);
            
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verify(messageRepository, times(1)).findByChatId(chatId, pageable);
        }
        
        @Test
        @DisplayName("Deve lançar MessageAccessDeniedException quando o usuário não for participante do chat")
        void findMessagesByChatId_ShouldThrowMessageAccessDeniedException_WhenUserIsNotParticipant() {
            Pageable pageable = PageRequest.of(0, 20);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(null);
            
            MessageAccessDeniedException exception = assertThrows(
                    MessageAccessDeniedException.class,
                    () -> messageService.findMessagesByChatId(userId, chatId, pageable)
            );
            
            assertEquals("User is not a participant of this chat.", exception.getMessage());
            verify(chatParticipantRepository, times(1)).findByUserIdAndChatId(userId, chatId);
            verifyNoInteractions(messageRepository);
        }

        @Test
        @DisplayName("Deve repassar Pageable corretamente para o repositório")
        void findMessagesByChatId_ShouldForwardPageableToRepository() {
            Pageable pageable = PageRequest.of(2, 10);
            Slice<Message> slice = new SliceImpl<>(List.of(message), pageable, true);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(authorParticipant);
            when(messageRepository.findByChatId(chatId, pageable)).thenReturn(slice);

            Slice<Message> result = messageService.findMessagesByChatId(userId, chatId, pageable);

            assertTrue(result.hasNext());
            verify(messageRepository).findByChatId(chatId, pageable);
        }
    }
}
