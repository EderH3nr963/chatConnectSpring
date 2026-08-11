package com.example.chatConnectSpring.usuario.infrastructure.adapters.out.persistence.updateEmailToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UpdateEmailTokenJpaRepository extends JpaRepository<UpdateEmailTokenEntity, UUID> {
    Optional<UpdateEmailTokenEntity> findByToken(String token);
    
    @Modifying
    @Query("""
        UPDATE UpdateEmailTokenEntity t
        SET t.used = true
        WHERE t.id = :id
    """)
    int markAsUsed(@Param("id") UUID id);
}
