package com.example.chatConnectSpring.friendship.application.services;

import com.example.chatConnectSpring.friendship.application.exception.FriendshipAccessDeniedException;
import com.example.chatConnectSpring.friendship.application.exception.FriendshipAlreadyExistsException;
import com.example.chatConnectSpring.friendship.application.exception.FriendshipNotFoundException;
import com.example.chatConnectSpring.friendship.application.exception.InvalidFriendshipException;
import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShipStatus;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendShipRepository;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendshipNotificationPort;
import com.example.chatConnectSpring.user.application.service.UserService;
import com.example.chatConnectSpring.user.domain.model.User;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendShipServiceTest {

    @Mock
    private FriendShipRepository friendShipRepository;

    @Mock
    private UserService userService;

    @Mock
    private FriendshipNotificationPort friendshipNotificationPort;

    @InjectMocks
    private FriendShipService friendShipService;

    @Captor
    private ArgumentCaptor<FriendShip> friendshipCaptor;

    private FriendShip friendShip;
    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        this.user = user1;

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        this.anotherUser = user2;

        FriendShip friendShip = new FriendShip();
        friendShip.setId(UUID.randomUUID());
        friendShip.setUserId(user1.getId());
        friendShip.setAnotherUserId(user2.getId());
        friendShip.setStatus(FriendShipStatus.PENDING);
        friendShip.setInvitedAt(OffsetDateTime.now());
        this.friendShip = friendShip;
    }

    @Nested
    @DisplayName("Cenários do Método invite()")
    class Invite {

        @Test
        @DisplayName("Deve enviar solicitação de amizade com sucesso quando não houver relação prévia")
        void shouldInviteSuccessfullyWhenNoPriorRelation() {
            when(userService.findById(user.getId())).thenReturn(user);
            when(userService.findById(anotherUser.getId())).thenReturn(anotherUser);
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(null);
            when(friendShipRepository.save(any(FriendShip.class))).thenAnswer(i -> i.getArgument(0));

            FriendShip result = friendShipService.invite(user.getId(), anotherUser.getId());

            assertNotNull(result);
            assertEquals(user.getId(), result.getUserId());
            assertEquals(anotherUser.getId(), result.getAnotherUserId());
            assertEquals(FriendShipStatus.PENDING, result.getStatus());
            assertNotNull(result.getInvitedAt());

            verify(friendShipRepository, times(1)).save(friendshipCaptor.capture());
            FriendShip saved = friendshipCaptor.getValue();
            assertEquals(FriendShipStatus.PENDING, saved.getStatus());
            verify(friendshipNotificationPort, times(1)).notifyFriendRequestReceived(eq(anotherUser.getId()), any());
        }

        @Test
        @DisplayName("Deve permitir reenviar convite caso o anterior tenha sido REJECTED")
        void shouldReinviteSuccessfullyWhenPreviouslyRejected() {
            friendShip.setStatus(FriendShipStatus.REJECTED);
            when(userService.findById(user.getId())).thenReturn(user);
            when(userService.findById(anotherUser.getId())).thenReturn(anotherUser);
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);
            when(friendShipRepository.save(any(FriendShip.class))).thenAnswer(i -> i.getArgument(0));

            FriendShip result = friendShipService.invite(user.getId(), anotherUser.getId());

            assertNotNull(result);
            assertEquals(FriendShipStatus.PENDING, result.getStatus());
            verify(friendShipRepository, times(1)).save(friendShip);
            verify(friendshipNotificationPort, times(1)).notifyFriendRequestReceived(eq(anotherUser.getId()), any());
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendshipException ao tentar convidar a si mesmo")
        void shouldThrowWhenInvitingSelf() {
            assertThrows(InvalidFriendshipException.class, () ->
                    friendShipService.invite(user.getId(), user.getId())
            );
            verify(friendShipRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendshipException quando um dos usuários não existir")
        void shouldThrowWhenUserDoesNotExist() {
            when(userService.findById(user.getId())).thenReturn(user);
            when(userService.findById(anotherUser.getId())).thenReturn(null);

            assertThrows(InvalidFriendshipException.class, () ->
                    friendShipService.invite(user.getId(), anotherUser.getId())
            );
            verify(friendShipRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar FriendshipAlreadyExistsException se já forem amigos")
        void shouldThrowWhenAlreadyFriends() {
            friendShip.setStatus(FriendShipStatus.ACCEPTED);
            when(userService.findById(user.getId())).thenReturn(user);
            when(userService.findById(anotherUser.getId())).thenReturn(anotherUser);
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            assertThrows(FriendshipAlreadyExistsException.class, () ->
                    friendShipService.invite(user.getId(), anotherUser.getId())
            );
            verify(friendShipRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar FriendshipAlreadyExistsException se o convite já estiver pendente")
        void shouldThrowWhenInviteAlreadyPending() {
            friendShip.setStatus(FriendShipStatus.PENDING);
            when(userService.findById(user.getId())).thenReturn(user);
            when(userService.findById(anotherUser.getId())).thenReturn(anotherUser);
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            assertThrows(FriendshipAlreadyExistsException.class, () ->
                    friendShipService.invite(user.getId(), anotherUser.getId())
            );
            verify(friendShipRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários do Método findFriendShip()")
    class FindFriendShip {

        @Test
        @DisplayName("Deve retornar a amizade quando ela existir")
        void shouldReturnFriendshipWhenExists() {
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            FriendShip result = friendShipService.findFriendShip(user.getId(), anotherUser.getId());

            assertNotNull(result);
            assertEquals(friendShip.getId(), result.getId());
        }

        @Test
        @DisplayName("Deve lançar FriendshipNotFoundException quando não existir relação")
        void shouldThrowWhenNotFound() {
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(null);

            assertThrows(FriendshipNotFoundException.class, () ->
                    friendShipService.findFriendShip(user.getId(), anotherUser.getId())
            );
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendshipException quando ID for nulo")
        void shouldThrowWhenNullId() {
            assertThrows(InvalidFriendshipException.class, () ->
                    friendShipService.findFriendShip(null, anotherUser.getId())
            );
        }
    }

    @Nested
    @DisplayName("Cenários do Método acceptInvite()")
    class AcceptInvite {

        @Test
        @DisplayName("Deve aceitar convite com sucesso pelo destinatário")
        void shouldAcceptInviteSuccessfully() {
            when(friendShipRepository.findBetweenUsers(anotherUser.getId(), user.getId())).thenReturn(friendShip);
            when(friendShipRepository.save(any(FriendShip.class))).thenAnswer(i -> i.getArgument(0));

            FriendShip result = friendShipService.acceptInvite(anotherUser.getId(), user.getId());

            assertNotNull(result);
            assertEquals(FriendShipStatus.ACCEPTED, result.getStatus());
            assertNotNull(result.getAcceptedAt());
            verify(friendShipRepository, times(1)).save(friendShip);
            verify(friendshipNotificationPort, times(1)).notifyFriendRequestAccepted(eq(user.getId()), any());
        }

        @Test
        @DisplayName("Deve lançar FriendshipAccessDeniedException quando o próprio remetente tentar aceitar")
        void shouldThrowWhenSenderTriesToAccept() {
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            assertThrows(FriendshipAccessDeniedException.class, () ->
                    friendShipService.acceptInvite(user.getId(), anotherUser.getId())
            );
            verify(friendShipRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar FriendshipNotFoundException quando não houver convite")
        void shouldThrowWhenInviteNotFound() {
            when(friendShipRepository.findBetweenUsers(anotherUser.getId(), user.getId())).thenReturn(null);

            assertThrows(FriendshipNotFoundException.class, () ->
                    friendShipService.acceptInvite(anotherUser.getId(), user.getId())
            );
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendshipException quando status não for PENDING")
        void shouldThrowWhenInviteNotPending() {
            friendShip.setStatus(FriendShipStatus.ACCEPTED);
            when(friendShipRepository.findBetweenUsers(anotherUser.getId(), user.getId())).thenReturn(friendShip);

            assertThrows(InvalidFriendshipException.class, () ->
                    friendShipService.acceptInvite(anotherUser.getId(), user.getId())
            );
        }
    }

    @Nested
    @DisplayName("Cenários do Método rejectInvite()")
    class RejectInvite {

        @Test
        @DisplayName("Deve rejeitar convite com sucesso pelo destinatário")
        void shouldRejectInviteSuccessfully() {
            when(friendShipRepository.findBetweenUsers(anotherUser.getId(), user.getId())).thenReturn(friendShip);
            when(friendShipRepository.save(any(FriendShip.class))).thenAnswer(i -> i.getArgument(0));

            friendShipService.rejectInvite(anotherUser.getId(), user.getId());

            assertEquals(FriendShipStatus.REJECTED, friendShip.getStatus());
            verify(friendShipRepository, times(1)).save(friendShip);
        }

        @Test
        @DisplayName("Deve lançar FriendshipAccessDeniedException quando remetente tentar rejeitar")
        void shouldThrowWhenSenderTriesToReject() {
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            assertThrows(FriendshipAccessDeniedException.class, () ->
                    friendShipService.rejectInvite(user.getId(), anotherUser.getId())
            );
            verify(friendShipRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários do Método cancelInvite()")
    class CancelInvite {

        @Test
        @DisplayName("Deve cancelar convite com sucesso pelo remetente")
        void shouldCancelInviteSuccessfully() {
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            friendShipService.cancelInvite(user.getId(), anotherUser.getId());

            verify(friendShipRepository, times(1)).delete(friendShip.getId());
        }

        @Test
        @DisplayName("Deve lançar FriendshipAccessDeniedException quando destinatário tentar cancelar")
        void shouldThrowWhenRecipientTriesToCancel() {
            when(friendShipRepository.findBetweenUsers(anotherUser.getId(), user.getId())).thenReturn(friendShip);

            assertThrows(FriendshipAccessDeniedException.class, () ->
                    friendShipService.cancelInvite(anotherUser.getId(), user.getId())
            );
            verify(friendShipRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Cenários do Método removeFriend()")
    class RemoveFriend {

        @Test
        @DisplayName("Deve remover amizade com sucesso quando aceita")
        void shouldRemoveFriendSuccessfully() {
            friendShip.setStatus(FriendShipStatus.ACCEPTED);
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            friendShipService.removeFriend(user.getId(), anotherUser.getId());

            verify(friendShipRepository, times(1)).delete(friendShip.getId());
            verify(friendshipNotificationPort, times(1)).notifyFriendshipRemoved(anotherUser.getId(), user.getId());
        }

        @Test
        @DisplayName("Deve lançar InvalidFriendshipException se não for amizade aceita")
        void shouldThrowWhenNotAccepted() {
            friendShip.setStatus(FriendShipStatus.PENDING);
            when(friendShipRepository.findBetweenUsers(user.getId(), anotherUser.getId())).thenReturn(friendShip);

            assertThrows(InvalidFriendshipException.class, () ->
                    friendShipService.removeFriend(user.getId(), anotherUser.getId())
            );
            verify(friendShipRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Cenários de Listagens")
    class Listings {

        @Test
        @DisplayName("Deve listar amigos aceitos")
        void shouldListFriends() {
            friendShip.setStatus(FriendShipStatus.ACCEPTED);
            when(friendShipRepository.findAcceptedFriends(user.getId())).thenReturn(List.of(friendShip));

            List<FriendShip> friends = friendShipService.listFriends(user.getId());

            assertEquals(1, friends.size());
            assertEquals(FriendShipStatus.ACCEPTED, friends.get(0).getStatus());
        }

        @Test
        @DisplayName("Deve listar convites pendentes recebidos")
        void shouldListPendingReceived() {
            when(friendShipRepository.findPendingReceived(anotherUser.getId())).thenReturn(List.of(friendShip));

            List<FriendShip> pending = friendShipService.listPendingReceivedInvites(anotherUser.getId());

            assertEquals(1, pending.size());
        }

        @Test
        @DisplayName("Deve listar convites pendentes enviados")
        void shouldListPendingSent() {
            when(friendShipRepository.findPendingSent(user.getId())).thenReturn(List.of(friendShip));

            List<FriendShip> pending = friendShipService.listPendingSentInvites(user.getId());

            assertEquals(1, pending.size());
        }
        
    }
}
