package com.example.chatConnectSpring.chat.infrastructure.mapper;

import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupStatus;
import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupTask;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.outbox.S3CleanupOutboxEntity;

public class S3CleanupOutboxMapper {

    public static S3CleanupOutboxEntity toEntity(S3CleanupTask d) {
        if (d == null) return null;
        S3CleanupOutboxEntity e = new S3CleanupOutboxEntity();
        e.setId(d.getId());
        e.setS3Key(d.getS3Key());
        e.setMessageId(d.getMessageId());
        e.setChatId(d.getChatId());
        e.setStatus(d.getStatus() != null ? d.getStatus().name() : S3CleanupStatus.PENDING.name());
        e.setAttempts(d.getAttempts());
        e.setLastError(d.getLastError());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        e.setProcessedAt(d.getProcessedAt());
        return e;
    }

    public static S3CleanupTask toDomain(S3CleanupOutboxEntity e) {
        if (e == null) return null;
        S3CleanupTask d = new S3CleanupTask();
        d.setId(e.getId());
        d.setS3Key(e.getS3Key());
        d.setMessageId(e.getMessageId());
        d.setChatId(e.getChatId());
        d.setStatus(S3CleanupStatus.valueOf(e.getStatus()));
        d.setAttempts(e.getAttempts());
        d.setLastError(e.getLastError());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        d.setProcessedAt(e.getProcessedAt());
        return d;
    }
}
