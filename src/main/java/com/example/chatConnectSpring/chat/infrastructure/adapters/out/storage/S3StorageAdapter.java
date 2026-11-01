package com.example.chatConnectSpring.chat.infrastructure.adapters.out.storage;

import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
public class S3StorageAdapter implements StoragePort {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.endpoint:${spring.cloud.aws.endpoint:http://localstack:4566}}")
    private String endpoint;

    @Value("${aws.s3.public-endpoint:}")
    private String publicEndpoint;

    public S3StorageAdapter(S3Presigner s3Presigner, @org.springframework.beans.factory.annotation.Autowired(required = false) S3Client s3Client) {
        this.s3Presigner = s3Presigner;
        this.s3Client = s3Client;
    }

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

    @Override
    public void deleteObject(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) return;
        if (s3Client == null) {
            throw new IllegalStateException("S3Client not configured");
        }
        s3Client.deleteObject(b -> b.bucket(bucketName).key(objectKey).build());
    }

    private String rewriteForFrontend(String url) {
        if (publicEndpoint == null || publicEndpoint.isBlank() || endpoint == null || endpoint.isBlank()) return url;
       
        if (!url.contains(endpoint)) return url;
       
        try {
            String urlHost = java.net.URI.create(url).getHost();
            String endpointHost = java.net.URI.create(endpoint).getHost();
            if (urlHost != null && urlHost.equals(endpointHost)) {
                return url.replace(endpoint, publicEndpoint);
            }
        } catch (Exception ignored) {}
        return url;
    }
}
