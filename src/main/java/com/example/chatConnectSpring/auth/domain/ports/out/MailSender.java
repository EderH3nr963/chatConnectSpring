package com.example.chatConnectSpring.auth.domain.ports.out;

import com.example.chatConnectSpring.auth.domain.model.vo.Email;
import com.example.chatConnectSpring.auth.domain.model.vo.ResetCode;

public interface MailSender {

    void sendResetCode(Email to, ResetCode code);
}
