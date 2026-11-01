package com.example.chatConnectSpring.friendship.application.listeners;

import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.ports.out.ChatRepository;
import com.example.chatConnectSpring.friendship.application.events.UserBlockedEvent;
import com.example.chatConnectSpring.friendship.application.events.UserUnblockedEvent;
import com.example.chatConnectSpring.friendship.domain.ports.out.UserBlockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bloqueio agora marca o CHAT privado como bloqueado, sem tocar na amizade.
 * Ao desbloquear, apenas desmarca o chat - a amizade permanece como estiver.
 * userBlock deixa de ver amizades e foca em bloquear/desbloquear chat.
 */
@Component
public class FriendshipBlockEventListener {

    private static final Logger log = LoggerFactory.getLogger(FriendshipBlockEventListener.class);

    private final ChatRepository chatRepository;
    private final UserBlockRepository userBlockRepository;

    public FriendshipBlockEventListener(ChatRepository chatRepository, UserBlockRepository userBlockRepository) {
        this.chatRepository = chatRepository;
        this.userBlockRepository = userBlockRepository;
    }

    @EventListener
    @Transactional
    public void onUserBlocked(UserBlockedEvent event) {
        if (event == null || event.blockerId() == null || event.blockedId() == null) {
            return;
        }

        Chat chat = chatRepository.findPrivateChat(event.blockerId(), event.blockedId());
        if (chat == null) {
            log.debug("Block event: sem chat privado entre {} e {}, nada a bloquear", event.blockerId(), event.blockedId());
            return;
        }

        if (chat.isBlocked()) {
            log.debug("Chat {} ja esta bloqueado", chat.getId());
            return;
        }

        log.info("Block event: chat privado {} entre {} e {} sera marcado como bloqueado", chat.getId(), event.blockerId(), event.blockedId());
        chatRepository.blockPrivateChat(chat.getId());
        log.info("Chat {} bloqueado via UserBlockedEvent (ainda verifica userblock em ambos sentidos)", chat.getId());
    }

    @EventListener
    @Transactional
    public void onUserUnblocked(UserUnblockedEvent event) {
        if (event == null || event.blockerId() == null || event.blockedId() == null) {
            return;
        }

        Chat chat = chatRepository.findPrivateChat(event.blockerId(), event.blockedId());
        if (chat == null) {
            log.debug("Unblock event: sem chat privado entre {} e {}, nada a desbloquear", event.blockerId(), event.blockedId());
            return;
        }

        if (!chat.isBlocked()) {
            log.debug("Unblock event: chat {} nao esta bloqueado, nada a alterar", chat.getId());
            return;
        }

        boolean stillBlockedByBlocker = userBlockRepository.existsByBlockerIdAndBlockedId(event.blockerId(), event.blockedId());
        boolean stillBlockedByOther = userBlockRepository.existsByBlockerIdAndBlockedId(event.blockedId(), event.blockerId());

        if (stillBlockedByBlocker || stillBlockedByOther) {
            log.info("Unblock event: chat {} permanece bloqueado pois ainda existe bloqueio {}->{}={} | {}->{}={}",
                    chat.getId(), event.blockerId(), event.blockedId(), stillBlockedByBlocker,
                    event.blockedId(), event.blockerId(), stillBlockedByOther);
            return;
        }

        log.info("Unblock: desbloqueando chat {} entre {} e {} (amizade preservada)", chat.getId(), event.blockerId(), event.blockedId());
        chatRepository.unblockPrivateChat(chat.getId());
        log.info("Chat {} desbloqueado apos unblock entre {} e {}", chat.getId(), event.blockerId(), event.blockedId());
    }
}
