package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UserIdentityProvider;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UserIdentityProviderAdapter
        implements UserIdentityProvider {
    
    private final UsuarioRepository usuarioRepository;
    
    public UserIdentityProviderAdapter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Override
    public User findByClerkUserId(String clerkUserId) {
        return usuarioRepository.findByClerkUserId(clerkUserId);
    }
}