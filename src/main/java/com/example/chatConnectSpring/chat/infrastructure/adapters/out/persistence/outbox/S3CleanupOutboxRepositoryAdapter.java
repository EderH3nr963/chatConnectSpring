package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.outbox;

import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupStatus;
import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupTask;
import com.example.chatConnectSpring.chat.domain.ports.out.S3CleanupOutboxRepository;
import com.example.chatConnectSpring.chat.infrastructure.mapper.S3CleanupOutboxMapper;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class S3CleanupOutboxRepositoryAdapter implements S3CleanupOutboxRepository {

    private final S3CleanupOutboxJpaRepository jpa;

    public S3CleanupOutboxRepositoryAdapter(S3CleanupOutboxJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public S3CleanupTask save(S3CleanupTask task) {
        var entity = S3CleanupOutboxMapper.toEntity(task);
        return S3CleanupOutboxMapper.toDomain(jpa.save(entity));
    }

    @Override
    public List<S3CleanupTask> findByStatus(S3CleanupStatus status, int limit) {
        var all = jpa.findByStatusOrderByCreatedAtAsc(status.name());
        if (all.size() <= limit) return all.stream().map(S3CleanupOutboxMapper::toDomain).collect(Collectors.toList());
        return all.subList(0, limit).stream().map(S3CleanupOutboxMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<S3CleanupTask> findPendingBatch(int limit) {
        return findByStatus(S3CleanupStatus.PENDING, limit);
    }

    @Override
    public void markDone(UUID id) {
        var entity = jpa.findById(id).orElse(null);
        if (entity == null) return;
        entity.setStatus(S3CleanupStatus.DONE.name());
        entity.setProcessedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        jpa.save(entity);
    }

    @Override
    public void markFailed(UUID id, String error) {
        var entity = jpa.findById(id).orElse(null);
        if (entity == null) return;
        entity.setStatus(S3CleanupStatus.FAILED.name());
        entity.setAttempts(entity.getAttempts() + 1);
        entity.setLastError(error != null && error.length() > 1000 ? error.substring(0, 1000) : error);
        entity.setUpdatedAt(OffsetDateTime.now());
        jpa.save(entity);
    }

    @Override
    public void deleteDoneOlderThan(OffsetDateTime cutoff) {
        jpa.deleteDoneOlderThan(cutoff);
    }

    @Override
    public long countByStatus(S3CleanupStatus status) {
        return jpa.countByStatus(status.name());
    }
}
