package com.example.chatConnectSpring.chat.infrastructure.adapters.in.http.dto.response;

import com.example.chatConnectSpring.chat.domain.model.chatParticipant.ChatParticipantRole;
import com.example.chatConnectSpring.user.infrastructure.adapters.in.http.dto.response.UserSummaryResponseDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatParticipantResponseDTO(
        UUID id,
        UUID chatId,
        OffsetDateTime lastReadMessage,
        UserSummaryResponseDTO user,
        OffsetDateTime joinedAt,
        ChatParticipantRole role
) {
}
