package com.example.chatConnectSpring.chat.domain.ports.out;

import java.time.Duration;

public interface StoragePort {
    String generatePresignedUploadUrl(String objectKey, String contentType, Duration expiration);

    String generatePresignedDownloadUrl(String objectKey, Duration expiration);

    default String generatePresignedDownloadUrl(String objectKey) {
        return generatePresignedDownloadUrl(objectKey, Duration.ofMinutes(60));
    }

    default void deleteObject(String objectKey) {
        throw new UnsupportedOperationException("deleteObject not implemented");
    }
}
