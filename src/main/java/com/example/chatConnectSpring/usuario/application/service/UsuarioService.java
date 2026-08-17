package com.example.chatConnectSpring.usuario.application.service;

import com.example.chatConnectSpring.shared.utils.GenerateToken;
import com.example.chatConnectSpring.usuario.domain.model.UpdateEmailToken;
import com.example.chatConnectSpring.usuario.domain.model.Usuario;
import com.example.chatConnectSpring.usuario.domain.port.in.*;
import com.example.chatConnectSpring.usuario.domain.port.out.EmailSender;
import com.example.chatConnectSpring.usuario.domain.port.out.UpdateEmailTokenRepository;
import com.example.chatConnectSpring.usuario.domain.port.out.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UsuarioService implements
        CreateUsuarioUseCase,
        FindByIdUseCase,
        FindByEmailUseCase,
        ConfirmEmailChangeUseCase,
        UpdateUsuarioUseCase,
        UpdatePasswordUseCase,
        DeleteUsuarioUseCase {
    
    @Value("${baseUrlFrontend")
    private String baseUrlFrontend;
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final UpdateEmailTokenRepository updateEmailTokenRepository;
    
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            EmailSender emailSender,
            UpdateEmailTokenRepository updateEmailTokenRepository
        ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.updateEmailTokenRepository = updateEmailTokenRepository;
    }
    
    @Override
    public Usuario create(String username, String email, String password) {
        
        validateAvailable(username, email, null);
        
        Usuario usuario = new Usuario();
        
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,17}$";
        Pattern padraoPassword = Pattern.compile(regex);
        
        if (!padraoPassword.matcher(password).matches())
            throw new CredenciaisInvalidasException("A senha deve conter números, letras maiúsculas e minúsculas, e caracteres especiais");
        
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        
        return usuarioRepository.create(usuario);
    }
    
    @Override
    @Transactional
    public Usuario update(UUID id, String username, String email) {
        
        Usuario usuario = findById(id);
        boolean usernameChanged = false;
        
        validateAvailable(username, email, id);
        
        if (username != null && !username.equals(usuario.getUsername())) {
            usuario.setUsername(username);
            usernameChanged = true;
        }

        if (usernameChanged) {
            usuarioRepository.save(usuario);
        }
        
        if (email == null || email.equals(usuario.getEmail()))
            return usuario;

        
        requestEmailChange(id, email);
        
        return usuario;
    }
    
    private void requestEmailChange(UUID userId, String newEmail) {
        
        String token = GenerateToken.generateSecureToken(32);
        
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(15);
        
        updateEmailTokenRepository.create(
                userId,
                newEmail,
                token,
                expiresAt
        );
        
        String confirmationLink =
                "http://{baseUrlFrontend}/usuarios/confirm-change-email#token="
                        + token;
        confirmationLink = confirmationLink.replace("{baseUrlFrontend}", baseUrlFrontend);
        
        emailSender.send(
                newEmail,
                "Confirmação de alteração de e-mail",
                "Clique no link para confirmar a alteração do seu e-mail:\n\n"
                        + confirmationLink
                        + "\n\n"
                        + "Este link expira em 15 minutos."
        );
    }
    
    @Transactional
    @Override
    public Usuario confirmEmailChange(UUID userId, String token) {
        
        UpdateEmailToken data =
                updateEmailTokenRepository.findByToken(token);
        
        if (data == null || data.getExpiresAt().isBefore(LocalDateTime.now()) || data.isUsed() || !data.getUserId().equals(userId)) {
            throw new IllegalArgumentException(
                    "Token inválido ou expirado"
            );
        }
        
        Usuario usuario = findById(data.getUserId());
        
        usuario.setEmail(data.getNewEmail());
        usuarioRepository.save(usuario);
        
        updateEmailTokenRepository.markAsUsed(data.getId());
        
        return usuario;
    }
    
    @Override
    public Usuario findById(UUID id) {
        
        Usuario usuario = usuarioRepository.findById(id);
        
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException();
        }
        
        return usuario;
    }
    
    @Override
    public Usuario findByEmail(String email) {
        
        Usuario usuario = usuarioRepository.findByEmail(email);
        
        if (usuario == null) {
            throw new UsuarioNaoEncontradoException();
        }
        
        return usuario;
    }
    
    @Override
    public void updatePassword(
            UUID id,
            String oldPassword,
            String newPassword
    ) {
        
        Usuario usuario = findById(id);
        
        if (!passwordEncoder.matches(
                oldPassword,
                usuario.getPassword()
        )) {
            throw new CredenciaisInvalidasException(
                    "A senha atual esta incorreta"
            );
        }
        
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }
    
    @Override
    public void delete(UUID id) {
        
        findById(id);
        
        usuarioRepository.deleteById(id);
    }
    
    private void validateAvailable(
            String username,
            String email,
            UUID actualId
    ) {
        
        if (username != null) {
            
            Usuario existente =
                    usuarioRepository.findByUsername(username);
            
            if (existente != null &&
                    !existente.getId().equals(actualId)) {
                
                throw new ConflitoUsuarioException(
                        "Nome de usuario ja esta em uso"
                );
            }
        }
        
        if (email != null) {
            
            Usuario existente =
                    usuarioRepository.findByEmail(email);
            
            if (existente != null &&
                    !existente.getId().equals(actualId)) {
                
                throw new ConflitoUsuarioException(
                        "E-mail ja esta em uso"
                );
            }
        }
    }
}
