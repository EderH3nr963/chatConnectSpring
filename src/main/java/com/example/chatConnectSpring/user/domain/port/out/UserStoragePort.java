package com.example.chatConnectSpring.user.domain.port.out;

import java.time.Duration;

public interface UserStoragePort {
    String generatePresignedUploadUrl(String objectKey, String contentType, Duration expiration);

    String generatePresignedDownloadUrl(String objectKey, Duration expiration);

    default String generatePresignedDownloadUrl(String objectKey) {
        return generatePresignedDownloadUrl(objectKey, Duration.ofMinutes(60));
    }
}
