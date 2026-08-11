package com.example.chatConnectSpring.auth.infrastructure.adapters.in;

import com.example.chatConnectSpring.auth.application.AuthService;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto.AuthResponseDTO;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto.CadastroRequestDTO;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDTO cadastrar(@Valid @RequestBody CadastroRequestDTO request) {
        return authService.cadastrar(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
}
