package com.example.chatConnectSpring.user.infrastructure.adapters.out.storage;

import com.example.chatConnectSpring.user.domain.port.out.UserStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3UserStorageAdapter implements UserStoragePort {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint:${spring.cloud.aws.endpoint:http://localstack:4566}}")
    private String endpoint;

    @Value("${aws.s3.public-endpoint:}")
    private String publicEndpoint;

    @Override
    public String generatePresignedUploadUrl(String objectKey, String contentType, Duration expiration) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return rewriteForFrontend(presignedRequest.url().toString());
    }

    @Override
    public String generatePresignedDownloadUrl(String objectKey, Duration expiration) {
        if (objectKey == null || objectKey.isBlank()) return null;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return rewriteForFrontend(presignedRequest.url().toString());
    }

    private String rewriteForFrontend(String url) {
        if (publicEndpoint == null || publicEndpoint.isBlank() || endpoint == null || endpoint.isBlank()) return url;
        if (!url.contains(endpoint)) return url;
        return url.replace(endpoint, publicEndpoint);
    }
}
