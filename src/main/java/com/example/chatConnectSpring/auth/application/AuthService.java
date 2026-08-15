package com.example.chatConnectSpring.auth.application;

import com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto.AuthResponseDTO;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto.CadastroRequestDTO;
import com.example.chatConnectSpring.auth.infrastructure.adapters.in.dto.LoginRequestDTO;
import com.example.chatConnectSpring.shared.security.JwtService;
import com.example.chatConnectSpring.usuario.application.mapper.UsuarioMapper;
import com.example.chatConnectSpring.usuario.application.service.UsuarioService;
import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioService usuarioService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO cadastrar(CadastroRequestDTO request) {
        Usuario usuario = usuarioService.create(request.username(), request.email(), request.password());
        return response(usuario);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("E-mail ou senha invalidos");
        }
        return response(usuarioService.findByEmail(request.email()));
    }

    private AuthResponseDTO response(Usuario usuario) {
        return new AuthResponseDTO(jwtService.generateToken(usuario.getId().toString()), UsuarioMapper.toDTO(usuario));
    }
}
