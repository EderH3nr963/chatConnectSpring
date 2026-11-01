package com.example.chatConnectSpring.auth.domain.ports.in;

import com.example.chatConnectSpring.auth.application.commands.PreRegisterCommand;
import com.example.chatConnectSpring.auth.application.commands.RegisterCommand;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;

public interface RegisterUseCase {
    void preRegister(PreRegisterCommand command);
    LoginResponse verifyRegistration(String email, String code);
}
