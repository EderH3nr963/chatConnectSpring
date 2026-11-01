package com.example.chatConnectSpring.chat.domain.ports.in;

import java.util.UUID;

public interface ConfirmMessageAttachmentUseCase {
    void execute(UUID messageId, String objectKey, long fileSize, String mimeType);
}
