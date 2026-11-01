package com.example.chatConnectSpring.chat.domain.ports.out.projections;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.chat.domain.model.message.Message;
import com.example.chatConnectSpring.chat.domain.ports.out.StoragePort;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatSummaryDto(
        UUID chatId,
        String name,
        ChatTypeEnum chatType,
        String avatarUrl,
        Boolean fixedChat,
        String lastMessageContent,
        OffsetDateTime lastMessageSentAt,
        long unreadCount,
        boolean blocked,
        OffsetDateTime blockedAt
) {
    public static ChatSummaryDto from(Chat chat, Message message, long unreadCount) {
        return from(chat, message, unreadCount, null);
    }

    public static ChatSummaryDto from(Chat chat, Message message, long unreadCount, StoragePort storagePort) {
        return new ChatSummaryDto(
                chat.getId(),
                chat.getName(),
                chat.getChatType(),
                resolve(chat.getAvatarKey(), storagePort),
                false,
                message != null ? message.getContent() : null,
                message != null ? message.getCreatedAt() : null,
                unreadCount,
                chat != null && chat.isBlocked(),
                chat != null ? chat.getBlockedAt() : null
        );
    }

    public static ChatSummaryDto fromPrivateChat(Chat chat, Message message, long unreadCount, String name, String avatarUrl) {
        return fromPrivateChat(chat, message, unreadCount, name, avatarUrl, null);
    }

    public static ChatSummaryDto fromPrivateChat(Chat chat, Message message, long unreadCount, String name, String avatarUrl, StoragePort storagePort) {
        return new ChatSummaryDto(
                chat.getId(),
                name,
                chat.getChatType(),
                resolve(avatarUrl, storagePort),
                false,
                message != null ? message.getContent() : null,
                message != null ? message.getCreatedAt() : null,
                unreadCount,
                chat != null && chat.isBlocked(),
                chat != null ? chat.getBlockedAt() : null
        );
    }

    private static String resolve(String avatarUrl, StoragePort storagePort) {
        if (storagePort == null || avatarUrl == null || avatarUrl.isBlank()) return avatarUrl;
        if (avatarUrl.startsWith("http://") || avatarUrl.startsWith("https://")) return avatarUrl;
        try {
            return storagePort.generatePresignedDownloadUrl(avatarUrl, Duration.ofMinutes(60));
        } catch (Exception ignored) {
            return avatarUrl;
        }
    }
}
