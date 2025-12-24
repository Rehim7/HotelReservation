package com.example.hotelreservationsystem.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;

@Configuration
public class R2Config {
    @Value("${r2.accessKeyId}")
    private String accessKeyId;
    @Value("${r2.secretAccessKey}")
    private String secretAccessKey;
    @Value("${r2.endpointUrl}")
    private String endpointUrl;
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpointUrl))
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(false)
                .build();
    }
}