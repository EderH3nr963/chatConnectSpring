package com.example.chatConnectSpring.auth.infrastructure.adapters.out.persistence.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CredentialsJpaRepository extends JpaRepository<CredentialsEntity, UUID> {
    
    Optional<CredentialsEntity> findByEmail(String email);
    
    Optional<CredentialsEntity> findByUserId(UUID userId);
    
    @Query("""
        UPDATE CredentialsEntity c
        SET c.email = :newEmail
        WHERE c.userId = :userId
    """)
    @Modifying
    void updateEmailByUserId(@Param("userId") UUID userId, @Param("newEmail") String newEmail);
    
    boolean existsByEmail(String email);
    
    boolean existsByUserId(UUID userId);
}
