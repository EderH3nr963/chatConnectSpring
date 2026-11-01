package com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging.strategy;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class S3MediaStrategyFactory {
    
    private final List<S3MediaProcessorStrategy> strategies;
    
    public S3MediaStrategyFactory(List<S3MediaProcessorStrategy> strategies) {
        this.strategies = strategies;
    }
    
    public S3MediaProcessorStrategy getStrategy(String objectKey) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(objectKey))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma estratégia encontrada para o objectKey: " + objectKey));
    }
}