package com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging.strategy;

import com.fasterxml.jackson.databind.JsonNode;

public interface S3MediaProcessorStrategy {
    boolean supports(String objectKey);
    
    void process(String objectKey, JsonNode recordNode);
}