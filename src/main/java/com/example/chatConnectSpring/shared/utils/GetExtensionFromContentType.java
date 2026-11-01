package com.example.chatConnectSpring.shared.utils;

public class GetExtensionFromContentType {
    public static String get(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg", "image/jpg" -> ".jpg";
            default -> throw new IllegalArgumentException("Formato de imagem não suportado");
        };
    }
}
