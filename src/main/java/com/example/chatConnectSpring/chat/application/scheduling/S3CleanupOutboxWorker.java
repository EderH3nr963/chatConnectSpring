package com.example.chatConnectSpring.chat.application.scheduling;

import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupStatus;
import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupTask;
import com.example.chatConnectSpring.chat.domain.ports.out.S3CleanupOutboxRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class S3CleanupOutboxWorker {

    private static final Logger log = LoggerFactory.getLogger(S3CleanupOutboxWorker.class);

    private final S3CleanupOutboxRepository outboxRepository;
    private final StoragePort storagePort;

    @Value("${outbox.s3-cleanup.batch-size:50}")
    private int batchSize;

    @Value("${outbox.s3-cleanup.retention-days:7}")
    private int retentionDays;

    @Value("${outbox.s3-cleanup.max-attempts:5}")
    private int maxAttempts;

    public S3CleanupOutboxWorker(S3CleanupOutboxRepository outboxRepository, StoragePort storagePort) {
        this.outboxRepository = outboxRepository;
        this.storagePort = storagePort;
    }

    @Scheduled(fixedDelayString = "${outbox.s3-cleanup.interval-ms:600000}")
    @Transactional
    public void processPending() {
        List<S3CleanupTask> batch = outboxRepository.findPendingBatch(batchSize);
        if (batch.isEmpty()) {
            return;
        }
        log.info("S3 outbox worker: processing {} pending tasks", batch.size());

        for (S3CleanupTask task : batch) {
            String s3Key = task.getS3Key();
            try {
                storagePort.deleteObject(s3Key);
                outboxRepository.markDone(task.getId());
                log.debug("S3 outbox DONE s3Key={} taskId={}", s3Key, task.getId());
            } catch (Exception e) {
                
                String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                if (task.getAttempts() + 1 >= maxAttempts) {
                    outboxRepository.markFailed(task.getId(), msg);
                    log.warn("S3 outbox FAILED (max attempts) s3Key={} taskId={} error={}", s3Key, task.getId(), msg);
                    return;
                }
                
                outboxRepository.markFailed(task.getId(), msg);
                var failed = outboxRepository.findByStatus(S3CleanupStatus.FAILED, 1).stream()
                        .filter(t -> t.getId().equals(task.getId())).findFirst().orElse(null);
                if (failed != null) {
                    failed.setStatus(S3CleanupStatus.PENDING);
                    outboxRepository.save(failed);
                }
                
                log.warn("S3 outbox retry scheduled s3Key={} taskId={} attempts={} error={}", s3Key, task.getId(), task.getAttempts() + 1, msg);
            }
        }

        try {
            OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
            outboxRepository.deleteDoneOlderThan(cutoff);
        } catch (Exception e) {
            log.warn("S3 outbox prune failed: {}", e.getMessage());
        }
    }
}
