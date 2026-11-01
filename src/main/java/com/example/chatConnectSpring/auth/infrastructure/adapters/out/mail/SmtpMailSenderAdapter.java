package com.example.chatConnectSpring.auth.infrastructure.adapters.out.mail;

import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.ResetCode;
import com.example.chatConnectSpring.auth.domain.ports.out.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class SmtpMailSenderAdapter implements MailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpMailSenderAdapter.class);

    private final JavaMailSender javaMailSender;
    private final String from;

    public SmtpMailSenderAdapter(JavaMailSender javaMailSender,
                                 @Value("${auth.mail.from:no-reply@chatconnect.local}") String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    @Override
    public void sendResetCode(Email to, ResetCode code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to.value());
        message.setSubject("ChatConnect - Código de recuperação de senha");
        message.setText(
                "Seu código de recuperação é: " + code.value() + "\n\n" +
                "Ele expira em 15 minutos.\n" +
                "Se você não solicitou, ignore este e-mail."
        );

        try {
            javaMailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send reset code to {}", to.value(), ex);
        }

        log.info("Reset code sent to {}", to.value());
    }
}
