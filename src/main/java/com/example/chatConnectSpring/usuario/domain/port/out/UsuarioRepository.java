package com.example.chatConnectSpring.usuario.domain.port.out;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;

import java.util.UUID;
import java.util.List;

public interface UsuarioRepository {
    Usuario create(Usuario usuario);
    Usuario save(Usuario usuario);
    
    Usuario findById(UUID id);
    List<Usuario> findAll();
    
    Usuario findByUsername(String username);
    
    Usuario findByEmail(String email);

    void deleteById(UUID id);
    
}
