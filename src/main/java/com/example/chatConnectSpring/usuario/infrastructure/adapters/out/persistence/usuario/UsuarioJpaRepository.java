package com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.usuario;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByUsername(String username);
    
    Optional<UsuarioEntity> findByEmail(String email);
    
    @Modifying
    @Query("""
        UPDATE UsuarioEntity u
        SET u.username = :username
        WHERE u.id = :id
    """)
    int updateUsername(
            @Param("id") UUID id,
            @Param("username") String username
    );
    
    @Modifying
    @Query("""
        UPDATE UsuarioEntity u
        SET u.email = :email
        WHERE u.id = :id
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    int updateEmail(
            @Param("id") UUID id,
            @Param("email") String email
    );
    
    @Modifying
    @Query("""
        UPDATE UsuarioEntity u
        SET u.password = :password
        WHERE u.id = :id
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    int updatePassword(
            @Param("id") UUID id,
            @Param("password") String password
    );
}
