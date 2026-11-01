package com.example.chatConnectSpring.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${aws.s3.endpoint:${spring.cloud.aws.endpoint:http://localstack:4566}}")
    private String endpoint;

    @Value("${aws.s3.public-endpoint:}")
    private String publicEndpoint;

    @Value("${spring.cloud.aws.credentials.access-key:test}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secret-key:test}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static:us-east-1}")
    private String region;

    /**
     * Presigner para URLs que o BROWSER vai usar (upload/download direto).
     * Precisa assinar contra o endpoint público (localhost:4566).
     */
    @Bean
    @Primary
    public S3Presigner s3Presigner() {
        // Presigner DEVE assinar com o endpoint que o browser vai acessar (public)
        // Se assinar com o endpoint interno (http://localstack:4566), a assinatura falha no browser (host mismatch)
        String base = (publicEndpoint != null && !publicEndpoint.isBlank()) ? publicEndpoint : endpoint;
        if (base == null || base.isBlank()) base = "http://localstack:4566";
        return S3Presigner.builder()
                .endpointOverride(URI.create(base))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }

    /**
     * Cliente para operações SERVER-SIDE (delete, getObject, etc).
     * Roda DENTRO do container -> precisa do endpoint interno (http://localstack:4566),
     * nunca localhost, senão não resolve entre containers.
     */
    @Bean
    public S3Client s3Client() {
        String base = endpoint;
        if (base == null || base.isBlank()) base = "http://localstack:4566";
        return S3Client.builder()
                .endpointOverride(URI.create(base))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();
    }
}
