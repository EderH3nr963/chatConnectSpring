package com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging;

import com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging.strategy.S3MediaProcessorStrategy;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.messaging.strategy.S3MediaStrategyFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class S3MediaEventsListener {
    
    private static final Logger log = LoggerFactory.getLogger(S3MediaEventsListener.class);
    
    private final ObjectMapper objectMapper;
    private final S3MediaStrategyFactory strategyFactory;
    
    public S3MediaEventsListener(ObjectMapper objectMapper, S3MediaStrategyFactory strategyFactory) {
        this.objectMapper = objectMapper;
        this.strategyFactory = strategyFactory;
    }
    
    @SqsListener("s3-chat-media-events-queue")
    public void processS3Event(String messagePayload) throws JsonProcessingException {
        log.info("S3 event received payloadSize={}", messagePayload != null ? messagePayload.length() : 0);
        JsonNode rootNode = objectMapper.readTree(messagePayload);
        // NoSuchBucket no LocalStack geralmente eh payload invalido - loga antes de parsear
        if (!rootNode.has("Records") && !rootNode.has("Body") && !rootNode.has("Message")) {
            log.warn("Payload SQS sem Records/Body/Message. payload={}", messagePayload.length() > 800 ? messagePayload.substring(0, 800) : messagePayload);
        }
        // LocalStack via Spring Cloud AWS pode entregar envelope SQS { Body: "... Records ..." } ou direto { Records: [...] }
        if (rootNode.has("Body")) {
            String bodyStr = rootNode.path("Body").asText();
            if (bodyStr != null && !bodyStr.isBlank()) {
                try { rootNode = objectMapper.readTree(bodyStr); } catch (Exception ignored) {}
            }
        }
        // outro formato SNS/SQS: { Message: "{...}" }
        if (!rootNode.has("Records") && rootNode.has("Message")) {
            String msgStr = rootNode.path("Message").asText();
            if (msgStr != null && !msgStr.isBlank()) {
                try { rootNode = objectMapper.readTree(msgStr); } catch (Exception ignored) {}
            }
        }
        // URL-encoded key (ex: uploads%2Fchat%2Fmessage%2F...) - decodifica no processRecord
        JsonNode records = rootNode.path("Records");
        
        if (!records.isArray() || records.isEmpty()) {
            log.warn("Payload ignorado: campo 'Records' ausente ou vazio. payload={}", messagePayload.length() > 800 ? messagePayload.substring(0, 800) : messagePayload);
            return;
        }
        
        for (JsonNode record : records) {
            processRecord(record);
        }
    }
    
    private void processRecord(JsonNode record) {
        String eventSource = record.path("eventSource").asText();
        String eventName = record.path("eventName").asText();
        
        if (!"aws:s3".equals(eventSource) || !eventName.startsWith("ObjectCreated:")) {
            log.debug("Evento ignorado: source/name incompatível -> {} / {}", eventSource, eventName);
            return;
        }
        
        String objectKey = record.path("s3").path("object").path("key").asText();
        // LocalStack/AWS encoda a key como URL (uploads%2F...) em algumas versoes
        if (objectKey != null && objectKey.contains("%2F")) {
            try { objectKey = java.net.URLDecoder.decode(objectKey, java.nio.charset.StandardCharsets.UTF_8); } catch (Exception ignored) {}
        }
        
        try {
            S3MediaProcessorStrategy strategy = strategyFactory.getStrategy(objectKey);
            strategy.process(objectKey, record);
        } catch (IllegalArgumentException e) {
            log.warn("Chave S3 sem processador mapeado: {}", objectKey);
        } catch (Exception e) {
            log.error("Erro ao processar S3 event key={}", objectKey, e);
        }
    }
}