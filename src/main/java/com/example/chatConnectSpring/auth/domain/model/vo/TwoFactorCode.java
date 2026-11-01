package com.example.chatConnectSpring.auth.domain.model.vo;

import java.security.SecureRandom;

public final class TwoFactorCode {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final String value;

    private TwoFactorCode(String value) {
        this.value = value;
    }

    public static TwoFactorCode generate() {
        int number = 100000 + RANDOM.nextInt(900000);
        return new TwoFactorCode(String.valueOf(number));
    }

    public static TwoFactorCode of(String raw) {
        ensureValid(raw);
        return new TwoFactorCode(raw);
    }

    public boolean matches(TwoFactorCode other) {
        return value.equals(other.value);
    }

    public String value() {
        return value;
    }

    private static void ensureValid(String raw) {
        if (raw == null || !raw.matches("\\d{6}")) {
            throw new IllegalArgumentException("Two-factor code must be 6 digits");
        }
    }

    @Override
    public String toString() {
        return "******";
    }
}
