package com.example.chatConnectSpring.chat.domain.model.message;


public record SendMessageResult(
        Message message,
        String presignedUploadUrl
) {
    public boolean requiresUpload() {
        return presignedUploadUrl != null && !presignedUploadUrl.isBlank();
    }
}