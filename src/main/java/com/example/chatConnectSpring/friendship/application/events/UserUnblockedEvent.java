package com.example.chatConnectSpring.friendship.application.events;

import java.util.UUID;

public record UserUnblockedEvent(
        UUID blockerId,
        UUID blockedId
) {
}
