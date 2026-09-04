package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClerkUserDeletedDataDTO(
        String id,
        String object,
        Boolean deleted
) {}
