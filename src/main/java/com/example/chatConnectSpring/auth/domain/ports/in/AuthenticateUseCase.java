package com.example.chatConnectSpring.auth.domain.ports.in;

import com.example.chatConnectSpring.auth.application.commands.LoginCommand;
import com.example.chatConnectSpring.auth.application.responses.LoginResponse;
import com.example.chatConnectSpring.auth.domain.model.vo.RawRefreshToken;

public interface AuthenticateUseCase {

    LoginResponse login(LoginCommand command);
}
