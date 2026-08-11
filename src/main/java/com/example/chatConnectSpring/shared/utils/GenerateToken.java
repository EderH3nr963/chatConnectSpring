package com.example.chatConnectSpring.shared.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateToken {
    public static String generateSecureToken(int length) {
        byte[] bytes = new byte[length];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
