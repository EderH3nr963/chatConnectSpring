package com.example.chatConnectSpring.auth.infrastructure.adapters.out.mail;

import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.ResetCode;
import com.example.chatConnectSpring.auth.domain.ports.out.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Dev/local: não tenta SMTP. Só loga o código no console.
 * Ativo quando spring.profiles.active = dev, local ou default (sem prod).
 * Em prod, o SmtpMailSenderAdapter assume (via @ConditionalOnProperty).
 */
@Component
@Primary
@Profile({"dev", "local", "default"})
public class LoggingMailSenderAdapter implements MailSender {

    private static final Logger log = LoggerFactory.getLogger(LoggingMailSenderAdapter.class);

    @Override
    public void sendResetCode(Email to, ResetCode code) {
        // Sempre visível no dev — copie e use no /reset-password
        log.info("==================================================");
        log.info(" [DEV MAIL] Reset code for {} : {}", to.value(), code.value());
        log.info(" Use: POST /api/auth/reset-password { email, code, newPassword }");
        log.info("==================================================");
        // Também stdout para quem roda sem log aggregator
        System.out.println("[DEV MAIL] " + to.value() + " -> code: " + code.value());
    }
}
