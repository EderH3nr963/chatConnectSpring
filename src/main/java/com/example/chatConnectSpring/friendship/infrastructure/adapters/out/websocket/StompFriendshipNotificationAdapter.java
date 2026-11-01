package com.example.chatConnectSpring.friendship.infrastructure.adapters.out.websocket;

import com.example.chatConnectSpring.friendship.domain.model.friendShip.FriendShip;
import com.example.chatConnectSpring.friendship.domain.ports.out.FriendshipNotificationPort;
import com.example.chatConnectSpring.friendship.infrastructure.adapters.out.websocket.dto.FriendshipWebSocketEventDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StompFriendshipNotificationAdapter implements FriendshipNotificationPort {

    private final SimpMessagingTemplate messagingTemplate;
    
    private static final String USER_NOTIFICATIONS_QUEUE = "/queue/friendship/notifications";

    public StompFriendshipNotificationAdapter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void notifyFriendRequestReceived(UUID targetUserId, FriendShip friendShip) {
        if (targetUserId == null || friendShip == null) {
            return;
        }
        FriendshipWebSocketEventDTO event = FriendshipWebSocketEventDTO.requestReceived(targetUserId, friendShip);
        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                USER_NOTIFICATIONS_QUEUE,
                event
        );
    }

    @Override
    public void notifyFriendRequestAccepted(UUID targetUserId, FriendShip friendShip) {
        if (targetUserId == null || friendShip == null) {
            return;
        }
        FriendshipWebSocketEventDTO event = FriendshipWebSocketEventDTO.requestAccepted(targetUserId, friendShip);
        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                USER_NOTIFICATIONS_QUEUE,
                event
        );
    }

    @Override
    public void notifyFriendshipRemoved(UUID targetUserId, UUID removedByUserId) {
        if (targetUserId == null) {
            return;
        }
        FriendshipWebSocketEventDTO event = FriendshipWebSocketEventDTO.removed(targetUserId, removedByUserId);
        messagingTemplate.convertAndSendToUser(
                targetUserId.toString(),
                USER_NOTIFICATIONS_QUEUE,
                event
        );
    }
}
