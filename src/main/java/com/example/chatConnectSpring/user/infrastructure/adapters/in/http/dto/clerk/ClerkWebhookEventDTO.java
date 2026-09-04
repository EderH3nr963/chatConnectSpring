package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClerkWebhookEventDTO<T>(
        T data,
        String object,
        String type,
        Long timestamp,
        @JsonProperty("event_attributes") Map<String, Object> eventAttributes
) {}
