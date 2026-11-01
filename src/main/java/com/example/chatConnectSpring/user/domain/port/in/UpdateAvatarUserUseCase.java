package com.example.chatConnectSpring.user.domain.port.in;

import java.util.UUID;

public interface UpdateAvatarUserUseCase {
    void execute(UUID userId, String objectKey);
}
