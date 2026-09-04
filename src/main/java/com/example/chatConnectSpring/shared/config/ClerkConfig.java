package com.example.chatConnectSpring.shared.config;

import com.clerk.backend_api.Clerk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClerkConfig {
    @Value("${CLERK_SECRET_KEY}")
    private String clerkSecret;
    
    @Bean
    public Clerk clerkClient() {
        return Clerk.builder()
                .bearerAuth(clerkSecret)
                .build();
    }
}