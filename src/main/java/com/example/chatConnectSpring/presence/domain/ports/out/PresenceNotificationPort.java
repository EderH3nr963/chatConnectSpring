package com.example.chatConnectSpring.presence.domain.ports.out;

import com.example.chatConnectSpring.presence.domain.model.Presence;

import java.util.UUID;

public interface PresenceNotificationPort {

    void notifyFriends(UUID userId, Presence presence);
}
