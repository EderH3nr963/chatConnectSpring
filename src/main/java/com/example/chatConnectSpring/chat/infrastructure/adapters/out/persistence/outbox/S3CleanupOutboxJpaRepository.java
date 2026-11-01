package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface S3CleanupOutboxJpaRepository extends JpaRepository<S3CleanupOutboxEntity, UUID> {

    @Query(value = "SELECT * FROM s3_cleanup_outbox WHERE status = :status ORDER BY created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<S3CleanupOutboxEntity> findByStatusForUpdate(@Param("status") String status, @Param("limit") int limit);

    List<S3CleanupOutboxEntity> findByStatusOrderByCreatedAtAsc(String status);

    long countByStatus(String status);

    @Modifying
    @Query("DELETE FROM S3CleanupOutboxEntity e WHERE e.status = 'DONE' AND e.processedAt < :cutoff")
    int deleteDoneOlderThan(@Param("cutoff") OffsetDateTime cutoff);
}
