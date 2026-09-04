package com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.clerk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClerkEmailAddressDTO(
        String id,
        @JsonProperty("email_address") String emailAddress,
        String object,
        @JsonProperty("verification") Map<String, Object> verification,
        @JsonProperty("linked_to") List<Map<String, Object>> linkedTo
) {}
