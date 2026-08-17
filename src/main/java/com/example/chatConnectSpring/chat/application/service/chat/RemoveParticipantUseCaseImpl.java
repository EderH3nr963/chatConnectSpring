package com.example.chatConnectSpring.chat.application.service.chat;

import com.example.chatConnectSpring.chat.application.command.RemoveParticipantCommand;
import com.example.chatConnectSpring.chat.application.exception.ChatInvalidException;
import com.example.chatConnectSpring.chat.application.exception.ChatNotFoundException;
import com.example.chatConnectSpring.chat.application.exception.ForbbidenChatException;
import com.example.chatConnectSpring.chat.domain.model.Chat;
import com.example.chatConnectSpring.chat.domain.model.ChatParticipant;
import com.example.chatConnectSpring.chat.domain.port.in.chat.RemoveParticipantUseCase;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatParticipantRepository;
import com.example.chatConnectSpring.chat.domain.port.out.chat.ChatRepository;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ParticipantRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveParticipantUseCaseImpl implements RemoveParticipantUseCase {
    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;

    public RemoveParticipantUseCaseImpl(
            ChatRepository chatRepository,
            ChatParticipantRepository chatParticipantRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatParticipantRepository = chatParticipantRepository;
    }

    @Override
    @Transactional
    public void removeParticipant(RemoveParticipantCommand command) {
        if (command.chatId() == null || command.userId() == null || command.participantId() == null) {
            throw new ChatInvalidException("Chat ID, usuário ID e participante ID são necessários");
        }

        Chat chat = chatRepository.findById(command.chatId());
        if (chat == null) {
            throw new ChatNotFoundException("Chat não encontrado");
        }
        if (!chat.isActive()) {
            throw new ChatInvalidException("Chat está desativado");
        }
        
        ChatParticipant me = chatParticipantRepository.getParticipantByChatIdAndUserId(
                command.chatId(),
                command.userId()
        );
        if (me.getParticipantRole() != ParticipantRole.ADMIN) {
            throw new ForbbidenChatException("Somente o administrador pode remover participantes");
        }

        ChatParticipant participant = chatParticipantRepository.getParticipantByChatIdAndUserId(
                command.chatId(),
                command.participantId()
        );
        if (participant == null) {
            throw new ChatNotFoundException("Participante não encontrado");
        }
        if (participant.getParticipantRole() == ParticipantRole.ADMIN
                && participant.getUserId().equals(chat.getCreatedByUserId())) {
            throw new ChatInvalidException("Não é possível remover o administrador do chat");
        }

        chatParticipantRepository.removeParticipant(participant.getId());
    }
}
