package com.example.chatConnectSpring.presence.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendShipRepository;
import com.example.chatConnectSpring.presence.domain.model.Presence;
import com.example.chatConnectSpring.presence.domain.ports.out.PresenceNotificationPort;
import com.example.chatConnectSpring.presence.infrastructure.adapters.in.http.dto.PresenceResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StompPresenceNotificationAdapter implements PresenceNotificationPort {

    private final SimpMessagingTemplate messagingTemplate;
    private final FriendShipRepository friendshipRepository;

    public StompPresenceNotificationAdapter(SimpMessagingTemplate messagingTemplate,
                                            FriendShipRepository friendshipRepository) {
        this.messagingTemplate = messagingTemplate;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public void notifyFriends(UUID userId, Presence presence) {
        List<FriendShip> friends = friendshipRepository.findAcceptedFriends(userId);
        if (friends.isEmpty()) return;

        PresenceResponseDTO dto = PresenceResponseDTO.from(presence);

        for (FriendShip friendship : friends) {
            UUID friendId = friendship.getOtherUserId(userId);
            if (friendId == null) continue;
            messagingTemplate.convertAndSendToUser(
                    friendId.toString(),
                    "/queue/presence",
                    dto
            );
        }
    }
}
