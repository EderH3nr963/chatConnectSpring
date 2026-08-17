package com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.usuario;

import com.example.chatConnectSpring.usuario.application.mapper.UsuarioMapper;
import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import com.example.chatConnectSpring.usuario.domain.port.out.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.List;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {
    UsuarioJpaRepository usuarioJpaRepository;
    
    public UsuarioRepositoryAdapter (UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }
    
    @Override
    public Usuario create(Usuario usuario) {
        UsuarioEntity usuarioEntity = UsuarioMapper.toEntity(usuario);
        
        usuarioEntity = usuarioJpaRepository.save(usuarioEntity);
        
        return UsuarioMapper.toDomain(usuarioEntity);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity usuarioEntity = UsuarioMapper.toEntity(usuario);
        usuarioEntity = usuarioJpaRepository.save(usuarioEntity);
        return UsuarioMapper.toDomain(usuarioEntity);
    }
    
    @Override
    public Usuario findById(UUID id) {
        UsuarioEntity usuarioEntity = usuarioJpaRepository.findById(id).orElse(null);
        
        return UsuarioMapper.toDomain(usuarioEntity);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioJpaRepository.findAll().stream()
                .map(UsuarioMapper::toDomain)
                .toList();
    }
    
    @Override
    public Usuario findByUsername(String username) {
        UsuarioEntity usuarioEntity = usuarioJpaRepository.findByUsername(username).orElse(null);
        
        return UsuarioMapper.toDomain(usuarioEntity);
    }
    
    @Override
    public Usuario findByEmail(String email) {
        UsuarioEntity usuarioEntity = usuarioJpaRepository.findByEmail(email).orElse(null);
        
        return UsuarioMapper.toDomain(usuarioEntity);
    }
    
    @Override
    public void deleteById(UUID id) {
        usuarioJpaRepository.deleteById(id);
    }
}
