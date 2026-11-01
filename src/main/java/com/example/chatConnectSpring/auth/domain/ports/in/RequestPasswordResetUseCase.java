package com.example.chatConnectSpring.auth.domain.ports.in;

public interface RequestPasswordResetUseCase {

    void request(String email);
}
