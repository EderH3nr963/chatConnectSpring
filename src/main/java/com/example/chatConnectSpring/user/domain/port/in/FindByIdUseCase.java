package com.example.chatConnectSpring.user.domain.port.in;

import com.example.chatConnectSpring.user.domain.model.User;

import java.util.UUID;

public interface FindByIdUseCase {
    User findById(UUID id);
}
