package com.example.chatConnectSpring.shared.utils;

import java.util.UUID;

public class S3KeyUtils {
    
    public static String buildMessageFileKey(UUID chatId, UUID messageId, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String randomFileName = UUID.randomUUID() + extension;
        
        return String.format("uploads/chat/message/%s/%s/%s", chatId, messageId, randomFileName);
    }
    
    private static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        return fileName.substring(lastDotIndex).toLowerCase();
    }
}