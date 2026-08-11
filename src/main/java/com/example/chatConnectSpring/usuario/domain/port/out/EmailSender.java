package com.example.chatConnectSpring.usuario.domain.port.out;

import jakarta.validation.constraints.Email;

public interface EmailSender {
    void send(String to, String subject, String body);
}
