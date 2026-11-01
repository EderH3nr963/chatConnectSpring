package com.example.chatConnectSpring.chat.application.scheduling;

import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupStatus;
import com.example.chatConnectSpring.chat.domain.model.outbox.S3CleanupTask;
import com.example.chatConnectSpring.chat.domain.ports.out.S3CleanupOutboxRepository;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3CleanupOutboxWorkerTest {

    @Mock S3CleanupOutboxRepository outboxRepository;
    @Mock StoragePort storagePort;

    S3CleanupOutboxWorker worker;

    @BeforeEach
    void setUp() {
        worker = new S3CleanupOutboxWorker(outboxRepository, storagePort);
        ReflectionTestUtils.setField(worker, "batchSize", 10);
        ReflectionTestUtils.setField(worker, "retentionDays", 7);
        ReflectionTestUtils.setField(worker, "maxAttempts", 5);
    }

    @Test @DisplayName("deve apagar do S3 e marcar DONE quando sucesso")
    void shouldDeleteAndMarkDone() {
        S3CleanupTask task = S3CleanupTask.pending("chat/a/file.jpg", UUID.randomUUID(), UUID.randomUUID());
        when(outboxRepository.findPendingBatch(10)).thenReturn(List.of(task));

        worker.processPending();

        verify(storagePort).deleteObject("chat/a/file.jpg");
        verify(outboxRepository).markDone(task.getId());
    }

    @Test @DisplayName("não faz nada quando não há pendentes")
    void shouldDoNothingWhenEmpty() {
        when(outboxRepository.findPendingBatch(10)).thenReturn(List.of());

        worker.processPending();

        verifyNoInteractions(storagePort);
    }

    @Test @DisplayName("marca FAILED quando S3 falha repetidamente")
    void shouldMarkFailedWhenS3Throws() {
        S3CleanupTask task = S3CleanupTask.pending("chat/a/file.jpg", UUID.randomUUID(), UUID.randomUUID());
        task.setAttempts(4); // última tentativa
        when(outboxRepository.findPendingBatch(10)).thenReturn(List.of(task));
        doThrow(new RuntimeException("S3 down")).when(storagePort).deleteObject(anyString());

        worker.processPending();

        verify(outboxRepository).markFailed(eq(task.getId()), anyString());
    }

    @Test @DisplayName("re-enfileira em PENDING quando ainda tem retries")
    void shouldRetryWhenAttemptsRemain() {
        S3CleanupTask task = S3CleanupTask.pending("chat/a/file.jpg", UUID.randomUUID(), UUID.randomUUID());
        task.setAttempts(0);
        when(outboxRepository.findPendingBatch(10)).thenReturn(List.of(task));
        doThrow(new RuntimeException("timeout")).when(storagePort).deleteObject(anyString());
        S3CleanupTask failed = S3CleanupTask.pending("chat/a/file.jpg", task.getMessageId(), task.getChatId());
        failed.setId(task.getId());
        failed.setStatus(S3CleanupStatus.FAILED);
        failed.setAttempts(1);
        when(outboxRepository.findByStatus(eq(S3CleanupStatus.FAILED), eq(1))).thenReturn(List.of(failed));

        worker.processPending();

        verify(outboxRepository).markFailed(eq(task.getId()), anyString());
        verify(outboxRepository).save(argThat(t -> t.getStatus() == S3CleanupStatus.PENDING));
    }
}
