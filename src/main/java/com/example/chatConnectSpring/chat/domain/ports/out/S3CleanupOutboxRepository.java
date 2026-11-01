package com.example.chatConnectSpring.chat.domain.ports.out;

import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupStatus;
import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupTask;

import java.util.List;
import java.util.UUID;

public interface S3CleanupOutboxRepository {
    S3CleanupTask save(S3CleanupTask task);
    List<S3CleanupTask> findByStatus(S3CleanupStatus status, int limit);
    List<S3CleanupTask> findPendingBatch(int limit);
    void markDone(UUID id);
    void markFailed(UUID id, String error);
    void deleteDoneOlderThan(java.time.OffsetDateTime cutoff);
    long countByStatus(S3CleanupStatus status);
}
