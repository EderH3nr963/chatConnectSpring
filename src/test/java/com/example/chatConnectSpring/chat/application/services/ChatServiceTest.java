package com.example.chatConnectSpring.chat.application.services;

import com.example.chatConnectSpring.chat.application.commands.AddParticipantsCommand;
import com.example.chatConnectSpring.chat.application.commands.CreateGroupChatCommand;
import com.example.chatConnectSpring.chat.application.commands.CreatePrivateChatCommand;
import com.example.chatConnectSpring.chat.application.commands.UpdateChatCommand;
import com.example.chatConnectSpring.chat.application.exceptions.ChatAccessDeniedException;
import com.example.chatConnectSpring.chat.application.exceptions.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exceptions.InvalidChatException;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.chat.application.services.ChatService;
import com.example.chatConnectSpring.chat.domain.ports.in.MessageUseCase;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.FindByIdUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    
    @Mock
    private ChatRepository chatRepository;
    
    @Mock
    private ChatParticipantRepository chatParticipantRepository;
    
    @Mock
    private MessageUseCase messageUseCase;
    
    @Mock
    private FindByIdUseCase findByIdUseCase;
    
    @InjectMocks
    private ChatService chatService;
    
    @Captor
    private ArgumentCaptor<Set<ChatParticipant>> participantsSetCaptor;
    
    @Captor
    private ArgumentCaptor<Chat> chatCaptor;
    
    private UUID userId;
    private UUID creatorUserId;
    private UUID chatId;
    private UUID generatedChatId;
    private Chat chat;
    private ChatParticipant adminParticipant;
    private ChatParticipant loggedUserParticipant;
    
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        creatorUserId = UUID.randomUUID();
        chatId = UUID.randomUUID();
        generatedChatId = UUID.randomUUID();
        
        chat = new Chat();
        chat.setId(chatId);
        chat.setChatType(ChatTypeEnum.GROUP);
        chat.setTitle("Grupo Devs");
        chat.setDescription("Grupo de Testes");
        
        adminParticipant = new ChatParticipant();
        adminParticipant.setId(UUID.randomUUID());
        adminParticipant.setUserId(userId);
        adminParticipant.setChatId(chatId);
        adminParticipant.setRole(ChatParticipantRole.ADMIN);
        
        loggedUserParticipant = new ChatParticipant();
        loggedUserParticipant.setId(UUID.randomUUID());
        loggedUserParticipant.setUserId(userId);
        loggedUserParticipant.setChatId(chatId);
        loggedUserParticipant.setUsername("usuario_logado");
        loggedUserParticipant.setRole(ChatParticipantRole.DEFAULT);
    }
    
    @Nested
    @DisplayName("Cenários do Método createGroupChat()")
    class CreateGroupChatTests {
        
        @Test
        @DisplayName("Deve criar um chat em grupo com sucesso, salvando o criador como ADMIN e os demais como DEFAULT")
        void shouldCreateGroupChatSuccessfully() {
            UUID participant1Id = UUID.randomUUID();
            UUID participant2Id = UUID.randomUUID();
            CreateGroupChatCommand command = new CreateGroupChatCommand("Grupo de Estudos", "Chat para tirar dúvidas", List.of(participant1Id, participant2Id));
            
            when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
                Chat chatToSave = invocation.getArgument(0);
                chatToSave.setId(generatedChatId);
                return chatToSave;
            });
            
            Chat result = chatService.createGroupChat(creatorUserId, command);
            
            assertNotNull(result);
            assertEquals(generatedChatId, result.getId());
            assertEquals("Grupo de Estudos", result.getTitle());
            assertEquals("Chat para tirar dúvidas", result.getDescription());
            assertEquals(ChatTypeEnum.GROUP, result.getChatType());
            
            verify(chatRepository, times(1)).save(chatCaptor.capture());
            Chat savedChat = chatCaptor.getValue();
            assertEquals("Grupo de Estudos", savedChat.getTitle());
            
            verify(chatParticipantRepository, times(1)).saveAll(participantsSetCaptor.capture());
            Set<ChatParticipant> savedParticipants = participantsSetCaptor.getValue();
            
            assertEquals(3, savedParticipants.size()); // Criador + 2 participantes
            
            assertTrue(savedParticipants.stream().anyMatch(p ->
                    p.getUserId().equals(creatorUserId) &&
                            p.getChatId().equals(generatedChatId) &&
                            p.getRole() == ChatParticipantRole.ADMIN
            ));
            
            assertTrue(savedParticipants.stream().anyMatch(p ->
                    p.getUserId().equals(participant1Id) &&
                            p.getChatId().equals(generatedChatId) &&
                            p.getRole() == ChatParticipantRole.DEFAULT
            ));
            
            assertTrue(savedParticipants.stream().anyMatch(p ->
                    p.getUserId().equals(participant2Id) &&
                            p.getChatId().equals(generatedChatId) &&
                            p.getRole() == ChatParticipantRole.DEFAULT
            ));
        }
        
        @Test
        @DisplayName("Deve evitar duplicidade caso o ID do criador esteja também presente na lista de participantes do comando")
        void shouldNotDuplicateCreatorIfPresentInCommandParticipants() {
            CreateGroupChatCommand command = new CreateGroupChatCommand("Grupo Dev", "Descrição", List.of(creatorUserId));
            
            when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
                Chat chatToSave = invocation.getArgument(0);
                chatToSave.setId(generatedChatId);
                return chatToSave;
            });
            
            chatService.createGroupChat(creatorUserId, command);
            
            verify(chatParticipantRepository, times(1)).saveAll(participantsSetCaptor.capture());
            Set<ChatParticipant> savedParticipants = participantsSetCaptor.getValue();
            
            assertNotNull(savedParticipants);
            assertFalse(savedParticipants.isEmpty());
        }
        
        @Test
        @DisplayName("Deve criar o grupo com sucesso mesmo que a lista de convidados inicial esteja vazia")
        void shouldCreateGroupChatWhenParticipantsListIsEmpty() {
            CreateGroupChatCommand command = new CreateGroupChatCommand("Apenas Eu", "Grupo de notas", List.of());
            
            when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
                Chat chatToSave = invocation.getArgument(0);
                chatToSave.setId(generatedChatId);
                return chatToSave;
            });
            
            Chat result = chatService.createGroupChat(creatorUserId, command);
            
            assertNotNull(result);
            verify(chatParticipantRepository, times(1)).saveAll(participantsSetCaptor.capture());
            Set<ChatParticipant> savedParticipants = participantsSetCaptor.getValue();
            
            assertEquals(1, savedParticipants.size());
            ChatParticipant onlyParticipant = savedParticipants.iterator().next();
            assertEquals(creatorUserId, onlyParticipant.getUserId());
            assertEquals(ChatParticipantRole.ADMIN, onlyParticipant.getRole());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método createPrivateChat()")
    class CreatePrivateChatTests {
        
        @Test
        @DisplayName("Deve criar chat privado com sucesso quando os dados forem válidos")
        void shouldCreatePrivateChatSuccessfully() {
            UUID otherUserId = UUID.randomUUID();
            AddParticipantsCommand participants = new AddParticipantsCommand(List.of(otherUserId), chatId);
            CreatePrivateChatCommand command = new CreatePrivateChatCommand(participants);
            
            User mockUser = new User();
            mockUser.setId(otherUserId);
            mockUser.setUsername("dev_usuario");
            
            when(chatRepository.save(any(Chat.class))).thenAnswer(i -> {
                Chat c = i.getArgument(0);
                c.setId(chatId);
                return c;
            });
            when(findByIdUseCase.findById(otherUserId)).thenReturn(mockUser);
            
            Chat result = chatService.createPrivateChat(userId, command);
            
            assertNotNull(result);
            assertEquals("dev_usuario", result.getTitle());
            assertEquals(ChatTypeEnum.PRIVATE, result.getChatType());
            
            verify(chatParticipantRepository, times(2)).save(any(ChatParticipant.class));
        }
        
        @Test
        @DisplayName("Deve lançar InvalidChatException se a lista de participantes for nula ou não tiver exatamente 1 usuário")
        void shouldThrowInvalidChatExceptionWhenParticipantsInvalid() {
            CreatePrivateChatCommand emptyCommand = new CreatePrivateChatCommand(new AddParticipantsCommand(List.of(), chatId));
            
            InvalidChatException exception = assertThrows(
                    InvalidChatException.class,
                    () -> chatService.createPrivateChat(userId, emptyCommand)
            );
            
            assertEquals("A private chat must have exactly one other participant.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve lançar InvalidChatException se o usuário tentar criar chat privado consigo mesmo")
        void shouldThrowInvalidChatExceptionWhenCreatingChatWithSelf() {
            AddParticipantsCommand participants = new AddParticipantsCommand(List.of(userId), chatId);
            CreatePrivateChatCommand command = new CreatePrivateChatCommand(participants);
            
            InvalidChatException exception = assertThrows(
                    InvalidChatException.class,
                    () -> chatService.createPrivateChat(userId, command)
            );
            
            assertEquals("A private chat cannot be created with yourself.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Deve lançar InvalidChatException se o outro usuário não existir no banco")
        void shouldThrowInvalidChatExceptionWhenUserDoesNotExist() {
            UUID otherUserId = UUID.randomUUID();
            AddParticipantsCommand participants = new AddParticipantsCommand(List.of(otherUserId), chatId);
            CreatePrivateChatCommand command = new CreatePrivateChatCommand(participants);
            
            when(chatRepository.save(any(Chat.class))).thenAnswer(i -> {
                Chat c = i.getArgument(0);
                c.setId(chatId);
                return c;
            });
            when(findByIdUseCase.findById(otherUserId)).thenReturn(null);
            
            InvalidChatException exception = assertThrows(
                    InvalidChatException.class,
                    () -> chatService.createPrivateChat(userId, command)
            );
            
            assertEquals("User does not exist.", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método findChatById()")
    class FindChatByIdTests {
        
        @Test
        @DisplayName("Deve retornar o chat mantendo o título original quando for um GRUPO")
        void shouldReturnGroupChatWithOriginalTitle() {
            Chat groupChat = new Chat();
            groupChat.setId(chatId);
            groupChat.setTitle("Grupo de Futebol");
            groupChat.setChatType(ChatTypeEnum.GROUP);
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId))
                    .thenReturn(loggedUserParticipant);
            Mockito.when(chatRepository.findById(chatId))
                    .thenReturn(groupChat);
            
            Chat result = chatService.findChatById(userId, chatId);
            
            assertNotNull(result);
            assertEquals("Grupo de Futebol", result.getTitle());
            assertEquals(ChatTypeEnum.GROUP, result.getChatType());
            
            Mockito.verify(chatParticipantRepository, Mockito.never()).findByChatId(Mockito.any());
        }
        
        @Test
        @DisplayName("Deve retornar o chat privado alterando o título para o nome do outro participante")
        void shouldReturnPrivateChatWithOtherParticipantUsernameAsTitle() {
            UUID otherUserId = UUID.randomUUID();
            
            Chat privateChat = new Chat();
            privateChat.setId(chatId);
            privateChat.setTitle("Título Antigo/Original");
            privateChat.setChatType(ChatTypeEnum.PRIVATE);
            
            ChatParticipant otherParticipant = new ChatParticipant();
            otherParticipant.setId(UUID.randomUUID());
            otherParticipant.setUserId(otherUserId);
            otherParticipant.setChatId(chatId);
            otherParticipant.setUsername("marcos_dev");
            
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId))
                    .thenReturn(loggedUserParticipant);
            Mockito.when(chatRepository.findById(chatId))
                    .thenReturn(privateChat);
            Mockito.when(chatParticipantRepository.findByChatId(chatId))
                    .thenReturn(List.of(loggedUserParticipant, otherParticipant));
            
            Chat result = chatService.findChatById(userId, chatId);
            
            assertNotNull(result);
            assertEquals("marcos_dev", result.getTitle());
            assertEquals(ChatTypeEnum.PRIVATE, result.getChatType());
        }
        
        @Test
        @DisplayName("Deve lançar ChatAccessDeniedException quando o usuário não pertencer ao chat")
        void shouldThrowChatAccessDeniedExceptionWhenUserIsNotParticipant() {
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId))
                    .thenReturn(null);
            
            ChatAccessDeniedException exception = assertThrows(
                    ChatAccessDeniedException.class,
                    () -> chatService.findChatById(userId, chatId)
            );
            
            assertEquals("User does not have access to this chat.", exception.getMessage());
            Mockito.verify(chatRepository, Mockito.never()).findById(Mockito.any());
        }
        
        @Test
        @DisplayName("Deve lançar ChatNotFoundException quando o chat não for encontrado no repositório")
        void shouldThrowChatNotFoundExceptionWhenChatDoesNotExist() {
            Mockito.when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId))
                    .thenReturn(loggedUserParticipant);
            Mockito.when(chatRepository.findById(chatId))
                    .thenReturn(null);
            
            ChatNotFoundException exception = assertThrows(
                    ChatNotFoundException.class,
                    () -> chatService.findChatById(userId, chatId)
            );
            
            assertEquals("Chat not found: " + chatId, exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método update()")
    class UpdateTests {
        
        @Test
        @DisplayName("Deve atualizar o chat quando o usuário for ADMIN e o chat for um GRUPO")
        void shouldUpdateChatWhenUserIsAdminAndChatIsGroup() {
            UpdateChatCommand command = new UpdateChatCommand("Título Atualizado", "Nova Descrição", chatId);
            
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(adminParticipant);
            when(chatRepository.findById(chatId)).thenReturn(chat);
            when(chatRepository.update(any(Chat.class))).thenAnswer(i -> i.getArgument(0));
            
            Chat updated = chatService.update(userId, command);
            
            assertEquals("Título Atualizado", updated.getTitle());
            assertEquals("Nova Descrição", updated.getDescription());
            verify(chatRepository, times(1)).update(chat);
        }
        
        @Test
        @DisplayName("Deve ignorar atualização se o chat for do tipo PRIVATE")
        void shouldNotUpdateIfChatIsPrivate() {
            chat.setChatType(ChatTypeEnum.PRIVATE);
            UpdateChatCommand command = new UpdateChatCommand("Título Atualizado", "Nova Descrição", chatId);
            
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(adminParticipant);
            when(chatRepository.findById(chatId)).thenReturn(chat);
            
            Chat result = chatService.update(userId, command);
            
            assertEquals("Grupo Devs", result.getTitle());
            verify(chatRepository, never()).update(any());
        }
        
        @Test
        @DisplayName("Deve lançar ChatAccessDeniedException se o usuário não for ADMIN")
        void shouldThrowAccessDeniedWhenUserIsNotAdmin() {
            adminParticipant.setRole(ChatParticipantRole.DEFAULT);
            UpdateChatCommand command = new UpdateChatCommand("Título Atualizado", "Nova Descrição", chatId);
            
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(adminParticipant);
            
            assertThrows(
                    ChatAccessDeniedException.class,
                    () -> chatService.update(userId, command)
            );
            verify(chatRepository, never()).update(any());
        }
    }
    
    @Nested
    @DisplayName("Cenários do Método delete()")
    class DeleteTests {
        
        @Test
        @DisplayName("Deve deletar mensagens, participantes e o chat com sucesso quando o usuário for ADMIN")
        void shouldDeleteChatSuccessfullyWhenUserIsAdmin() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(adminParticipant);
            when(chatRepository.findById(chatId)).thenReturn(chat);
            
            assertDoesNotThrow(() -> chatService.delete(userId, chatId));
            
            verify(messageUseCase, times(1)).deleteByChatId(chatId);
            verify(chatParticipantRepository, times(1)).deleteAllByChatId(chatId);
            verify(chatRepository, times(1)).delete(chatId);
        }
        
        @Test
        @DisplayName("Deve lançar ChatAccessDeniedException quando o usuário não for participante do chat")
        void shouldThrowChatAccessDeniedExceptionWhenUserIsNotParticipant() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(null);
            
            ChatAccessDeniedException exception = assertThrows(
                    ChatAccessDeniedException.class,
                    () -> chatService.delete(userId, chatId)
            );
            
            assertEquals("User does not have access to this chat.", exception.getMessage());
            
            verify(chatRepository, never()).findById(any());
            verify(messageUseCase, never()).deleteByChatId(any());
            verify(chatParticipantRepository, never()).deleteAllByChatId(any());
            verify(chatRepository, never()).delete(any());
        }
        
        @Test
        @DisplayName("Deve lançar ChatAccessDeniedException quando o usuário pertencer ao chat mas não for ADMIN")
        void shouldThrowChatAccessDeniedExceptionWhenUserIsNotAdmin() {
            adminParticipant.setRole(ChatParticipantRole.DEFAULT);
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(adminParticipant);
            
            ChatAccessDeniedException exception = assertThrows(
                    ChatAccessDeniedException.class,
                    () -> chatService.delete(userId, chatId)
            );
            
            assertEquals("Only administrators can delete this chat.", exception.getMessage());
            
            verify(chatRepository, never()).findById(any());
            verify(messageUseCase, never()).deleteByChatId(any());
            verify(chatParticipantRepository, never()).deleteAllByChatId(any());
            verify(chatRepository, never()).delete(any());
        }
        
        @Test
        @DisplayName("Deve lançar ChatNotFoundException quando o usuário for ADMIN mas o chat não existir no repositório")
        void shouldThrowChatNotFoundExceptionWhenChatDoesNotExist() {
            when(chatParticipantRepository.findByUserIdAndChatId(userId, chatId)).thenReturn(adminParticipant);
            when(chatRepository.findById(chatId)).thenReturn(null);
            
            ChatNotFoundException exception = assertThrows(
                    ChatNotFoundException.class,
                    () -> chatService.delete(userId, chatId)
            );
            
            assertEquals("Chat not found: " + chatId, exception.getMessage());
            
            verify(messageUseCase, never()).deleteByChatId(any());
            verify(chatParticipantRepository, never()).deleteAllByChatId(any());
            verify(chatRepository, never()).delete(any());
        }
    }
}