package com.example.chatConnectSpring.shared.security;

import com.example.chatConnectSpring.usuario.domain.port.out.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }

    @Override
    public UserDetailsImpl loadUserByUsername(String email) {
        var usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) throw new UsernameNotFoundException("Usuario nao encontrado");
        
        return new UserDetailsImpl(usuario);
    }
}
