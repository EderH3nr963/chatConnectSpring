package com.example.chatConnectSpring.chat.domain.model.outbox;

import java.time.OffsetDateTime;
import java.util.UUID;

public class S3CleanupTask {
    private UUID id;
    private String s3Key;
    private UUID messageId;
    private UUID chatId;
    private S3CleanupStatus status;
    private int attempts;
    private String lastError;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime processedAt;

    public S3CleanupTask() {}

    public static S3CleanupTask pending(String s3Key, UUID messageId, UUID chatId) {
        S3CleanupTask t = new S3CleanupTask();
        t.id = UUID.randomUUID();
        t.s3Key = s3Key;
        t.messageId = messageId;
        t.chatId = chatId;
        t.status = S3CleanupStatus.PENDING;
        t.attempts = 0;
        t.createdAt = OffsetDateTime.now();
        t.updatedAt = OffsetDateTime.now();
        return t;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
    public UUID getMessageId() { return messageId; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }
    public UUID getChatId() { return chatId; }
    public void setChatId(UUID chatId) { this.chatId = chatId; }
    public S3CleanupStatus getStatus() { return status; }
    public void setStatus(S3CleanupStatus status) { this.status = status; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public OffsetDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(OffsetDateTime processedAt) { this.processedAt = processedAt; }
}
