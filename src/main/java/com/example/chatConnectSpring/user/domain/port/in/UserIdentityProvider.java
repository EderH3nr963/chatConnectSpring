package com.example.chatConnectSpring.user.domain.port.in;

import com.example.chatConnectSpring.user.domain.model.User;

public interface UserIdentityProvider {
    
    User findByClerkUserId(String clerkUserId);
}