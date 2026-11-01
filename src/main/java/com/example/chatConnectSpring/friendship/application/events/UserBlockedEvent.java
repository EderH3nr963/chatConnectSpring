package com.example.chatConnectSpring.friendship.application.events;

import java.util.UUID;

public record UserBlockedEvent(
        UUID blockerId,
        UUID blockedId
) {
}
