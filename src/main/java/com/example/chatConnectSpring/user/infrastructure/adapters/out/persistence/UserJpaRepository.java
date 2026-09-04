package com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByClerkUserId(String clerkUserId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByClerkUserId(String clerkUserId);
    boolean existsByEmail(String email);
    void deleteByClerkUserId(String clerkUserId);
}
