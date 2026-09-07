package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.exceptions.ParticipantNotFoundException;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.PresenceStateOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagePresenceServiceTest {
    
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    
    @Mock
    private PresenceStateOutputPort presenceStateOutputPort;
    
    @InjectMocks
    private ManagePresenceService managePresenceService;
    
    @Captor
    private ArgumentCaptor<ChatParticipant> participantCaptor;
    
    private UUID userId;
    private UUID chatId;
    private ChatParticipant participant;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        
        participant = new ChatParticipant();
        participant.setId(UUID.randomUUID());
        participant.setUserId(userId);
        participant.setChatId(chatId);
        participant.setUnreadMessages(5);
    }
    
    @Nested
    @DisplayName("Cenários do Método enterChat()")
    class EnterChatTests {
        
        @Test
        @DisplayName("Deve zerar mensagens não lidas, salvar o participante e registrar a presença no OutputPort")
        void shouldEnterChatSuccessfully() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId))
                    .thenReturn(participant);
            
            assertDoesNotThrow(() -> managePresenceService.enterChat(userId, chatId));
            
            verify(chatParticipantRepository, times(1)).save(participantCaptor.capture());
            ChatParticipant savedParticipant = participantCaptor.getValue();
            assertEquals(0, savedParticipant.getUnreadMessages());
            
            verify(presenceStateOutputPort, times(1))
                    .setUserActiveInChat(chatId.toString(), userId.toString());
        }
        
        @Test
        @DisplayName("Deve lançar ParticipantNotFoundException quando o participante não pertencer ao chat")
        void shouldThrowParticipantNotFoundExceptionWhenUserIsNotParticipant() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId))
                    .thenReturn(null);
            
            ParticipantNotFoundException exception = assertThrows(
                    ParticipantNotFoundException.class,
                    () -> managePresenceService.enterChat(userId, chatId)
            );
            
            assertEquals("You are not logged in", exception.getMessage());
            
            verify(chatParticipantRepository, never()).save(any());
            verify(presenceStateOutputPort, never()).setUserActiveInChat(any(), any());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método exitChat()")
    class ExitChatTests {
        
        @Test
        @DisplayName("Deve remover o estado de usuário ativo no chat através da porta de saída")
        void shouldExitChatSuccessfully() {
            managePresenceService.exitChat(chatId, userId);
            
            verify(presenceStateOutputPort, times(1))
                    .clearUserActiveChat(chatId.toString(), userId.toString());
            
            verifyNoInteractions(chatParticipantRepository);
        }
    }
}