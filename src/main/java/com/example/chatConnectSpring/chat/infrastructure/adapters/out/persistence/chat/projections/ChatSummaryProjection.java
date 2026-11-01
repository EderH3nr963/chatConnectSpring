package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.projections;

import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;
import com.example.chatConnectSpring.chat.domain.ports.out.projections.ChatSummaryDto;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public record ChatSummaryProjection(
        UUID chatId,
        String name,
        String chatType,
        String avatarKey,
        Boolean fixedChat,
        String lastMessageContent,
        Instant lastMessageSentAt,
        Integer unreadCount,
        Boolean blocked,
        Instant blockedAt
) {
    public ChatSummaryDto toDto() {
        return toDto(null);
    }

    public ChatSummaryDto toDto(StoragePort storagePort) {
        String resolved = resolveAvatarUrl(avatarKey, storagePort);
        String avatarForDto = resolved != null ? resolved : avatarKey;
        System.out.println(avatarForDto);
        return new ChatSummaryDto(
                chatId,
                name,
                ChatTypeEnum.valueOf(chatType),
                avatarForDto,
                fixedChat,
                lastMessageContent,
                (lastMessageContent == null || lastMessageContent.isEmpty() || lastMessageSentAt == null) ? null : lastMessageSentAt.atOffset(ZoneOffset.UTC),
                unreadCount != null ? unreadCount.longValue() : 0L,
                Boolean.TRUE.equals(blocked),
                blockedAt != null ? blockedAt.atOffset(ZoneOffset.UTC) : null
        );
    }

    private static String resolveAvatarUrl(String avatarKey, StoragePort storagePort) {
        if (storagePort == null || avatarKey == null || avatarKey.isBlank()) return null;
        if (avatarKey.startsWith("http://") || avatarKey.startsWith("https://")) return avatarKey;
        try {
            return storagePort.generatePresignedDownloadUrl(avatarKey, Duration.ofMinutes(60));
        } catch (Exception ignored) {
            return null;
        }
    }
}