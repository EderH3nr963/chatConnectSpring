package com.example.chatConnectSpring.auth.domain.ports.in;

public interface ResetPasswordUseCase {

    void reset(String email, String code, String newPassword);
}
