package com.example.chatConnectSpring.usuario.domain.port.out;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;

import java.util.UUID;
import java.util.List;

public interface UsuarioRepository {
    public Usuario create(Usuario usuario);
    
    Usuario findById(UUID id);
    List<Usuario> findAll();
    
    public Usuario findByUsername(String username);
    
    public Usuario findByEmail(String email);
    
    void updateUsername(UUID id, String username);
    
    public void updateEmail(UUID id, String newEmail);
    
    public void updatePassword(UUID id, String newPassword);
    
    public void deleteById(UUID id);
    
}
