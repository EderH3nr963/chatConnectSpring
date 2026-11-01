package com.example.chatConnectSpring.auth.domain.model.vo;

import java.security.SecureRandom;

public final class ResetCode {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final String value;

    private ResetCode(String value) {
        this.value = value;
    }

    public static ResetCode generate() {
        int number = 100000 + RANDOM.nextInt(900000);
        return new ResetCode(String.valueOf(number));
    }

    public static ResetCode of(String raw) {
        if (raw == null || !raw.matches("\\d{6}")) {
            throw new IllegalArgumentException("Reset code must be 6 digits");
        }
        return new ResetCode(raw);
    }

    public String value() {
        return value;
    }

    public boolean matches(ResetCode other) {
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return "******";
    }
}
