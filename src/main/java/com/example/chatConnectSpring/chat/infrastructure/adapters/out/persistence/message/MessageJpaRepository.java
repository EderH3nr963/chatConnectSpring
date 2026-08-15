package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, UUID> {
}
