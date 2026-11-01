package com.example.chatConnectSpring.user.application.service;

import com.example.chatConnectSpring.user.application.exceptions.UserNotFoundException;
import com.example.chatConnectSpring.user.domain.model.User;
import com.example.chatConnectSpring.user.domain.port.in.UpdateAvatarUserUseCase;
import com.example.chatConnectSpring.user.domain.port.out.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateAvatarUserService implements UpdateAvatarUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateAvatarUserService.class);

    private final UsuarioRepository usuarioRepository;

    public UpdateAvatarUserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    @Override
    public void execute(UUID userId, String objectKey) {
        User user = usuarioRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        user.setAvatarKey(objectKey);
        usuarioRepository.save(user);
        log.info("Avatar do usuário {} atualizado com sucesso com a chave {}", userId, objectKey);
    }
}
