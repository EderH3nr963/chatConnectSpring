package com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.message;

import com.example.chatConnectSpring.chat.domain.model.message.MessageStatus;
import com.example.chatConnectSpring.chat.domain.model.message.MessageType;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chat.ChatEntity;
import com.example.chatConnectSpring.chat.infrastructure.adapters.out.persistence.chatParticipant.ChatParticipantEntity;
import com.example.chatConnectSpring.user.infrastructure.adapters.out.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "message",
    indexes = {
        @Index(name = "idx_message_chat_created", columnList = "chat_id, created_at"),
        @Index(name = "idx_message_sender_id", columnList = "sender_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    private UUID id;

    @Column(name = "chat_id", nullable = false)
    private UUID chatId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private UserEntity sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", insertable = false, updatable = false)
    private ChatEntity chat;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", columnDefinition = "message_status")
    private MessageStatus status;
    
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", columnDefinition = "attachment_type")
    private MessageType type;
    
    @Column(name = "file_s3_key")
    private String fileS3Key;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_mime_type")
    private String fileMimeType;
    
    @Column(nullable = false, length = 4000)
    private String content;

    @Column(name = "reply_id")
    private UUID replyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id", insertable = false, updatable = false)
    private MessageEntity reply;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
