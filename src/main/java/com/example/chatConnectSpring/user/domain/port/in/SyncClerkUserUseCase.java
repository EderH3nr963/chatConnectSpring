package com.example.chatConnectSpring.user.domain.port.in;

import com.example.chatConnectSpring.user.domain.model.User;

public interface SyncClerkUserUseCase {
    User syncFromClerk(String clerkUserId, String email, String username);
}
