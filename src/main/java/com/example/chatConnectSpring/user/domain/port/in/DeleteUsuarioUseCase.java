package com.example.chatConnectSpring.user.domain.port.in;

import java.util.UUID;

public interface DeleteUsuarioUseCase {
    void delete(UUID id);
    void deleteByClerkUserId(String clerkUserId);
}
