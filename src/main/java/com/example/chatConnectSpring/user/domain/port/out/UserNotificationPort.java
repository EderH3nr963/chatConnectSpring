package com.example.chatConnectSpring.user.domain.port.out;

import com.example.chatConnectSpring.user.domain.model.User;

import java.util.UUID;

public interface UserNotificationPort {
    void notifyUserCreated(User user);
    void notifyUserUpdated(User user);
    void notifyUserDeleted(UUID userId);
}
