package com.example.chatConnectSpring.chat.domain.model.message;

import java.util.Set;

public enum MessageType {
    TEXT,
    IMAGE,
    VOICE,
    VIDEO,
    DOCUMENT;
    
    public boolean isValidContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }
        
        String mime = contentType.toLowerCase().trim();
        
        return switch (this) {
            case TEXT -> mime.startsWith("text/") || mime.equals("application/json");
            
            case IMAGE -> mime.startsWith("image/") && (
                    mime.contains("jpeg") ||
                            mime.contains("png") ||
                            mime.contains("webp") ||
                            mime.contains("gif")
            );
            
            case VOICE -> mime.startsWith("audio/") && (
                    mime.contains("ogg") ||
                            mime.contains("mp4") ||
                            mime.contains("mpeg") ||
                            mime.contains("wav") ||
                            mime.contains("webm")
            );
            
            case VIDEO -> mime.startsWith("video/") && (
                    mime.contains("mp4") ||
                            mime.contains("webm") ||
                            mime.contains("quicktime") // .mov
            );
            
            case DOCUMENT -> ALLOWED_DOCUMENT_TYPES.contains(mime) ||
                    mime.startsWith("application/vnd.") ||
                    mime.startsWith("text/");
        };
    }
    
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
            "application/pdf",
            "application/zip",
            "application/x-rar-compressed",
            "application/x-zip-compressed",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
}