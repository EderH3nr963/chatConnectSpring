package com.example.chatConnectSpring.usuario.application.mapper;

import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import com.example.chatConnectSpring.usuario.infrastructure.adapters.in.dto.response.UsuarioResponseDTO;
import com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.usuario.UsuarioEntity;

public class UsuarioMapper {
    public static UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        
        entity.setId(usuario.getId());
        entity.setEmail(usuario.getEmail());
        entity.setPassword(usuario.getPassword());
        entity.setUsername(usuario.getUsername());
        
        return entity;
    }
    
    public static Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        Usuario domain = new Usuario();
        
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        domain.setPassword(entity.getPassword());
        domain.setUsername(entity.getUsername());
        
        return domain;
    }
    
    public static UsuarioResponseDTO toDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getUsername()
        );
    }
}
