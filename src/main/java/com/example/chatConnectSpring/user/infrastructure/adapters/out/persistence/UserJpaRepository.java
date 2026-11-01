package com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE u.id <> :excludeId
              AND (
                   LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            ORDER BY u.username ASC
            """)
    List<UserEntity> searchByUsernameOrEmail(@Param("q") String q, @Param("excludeId") UUID excludeId, Pageable pageable);
}
