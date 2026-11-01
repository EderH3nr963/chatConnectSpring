package com.example.chatConnectSpring.chat.domain.model.message;

import com.example.chatConnectSpring.chat.application.commands.AttachmentInfo;
import com.example.chatConnectSpring.chat.domain.model.chat.Chat;
import com.example.chatConnectSpring.chat.domain.model.chat.ChatTypeEnum;
import com.example.chatConnectSpring.shared.utils.S3KeyUtils;
import com.example.chatConnectSpring.user.domain.model.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Message {
    private UUID id;
    private String content;
    private MessageStatus status;
    private MessageType type;
    
    private UUID chatId;
    private Chat chat;
    
    private UUID senderId;
    private User sender;
    
    private String fileS3Key;
    private String fileName;
    private Long fileSize;
    private String fileMimeType;

    private UUID replyId;
    private Message reply;
    
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Message() {
    }
    
    public Message(UUID id, UUID chatId, UUID senderId, User sender, Chat chat, String content, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.sender = sender;
        this.chat = chat;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static Message create(UUID chatId, UUID senderId, MessageType type, String content, AttachmentInfo attachment) {
        return create(chatId, senderId, type, content, attachment, null);
    }

    public static Message create(UUID chatId, UUID senderId, MessageType type, String content, AttachmentInfo attachment, UUID replyId) {
        Message msg = new Message();
        msg.setId(UUID.randomUUID());
        msg.setChatId(chatId);
        msg.setSenderId(senderId);
        msg.setType(type);
        msg.setContent(content != null ? content.trim() : "");
        msg.setReplyId(replyId);

        boolean isMedia = type != MessageType.TEXT;
        msg.setStatus(isMedia ? MessageStatus.PENDING : MessageStatus.SENT);

        if (isMedia && attachment != null) {
            msg.setFileName(attachment.fileName());
            msg.setFileMimeType(attachment.contentType());
            msg.setFileS3Key(S3KeyUtils.buildMessageFileKey(chatId, msg.getId(), attachment.fileName()));
        }

        return msg;
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public Chat getChat() {
        return chat;
    }
    
    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public String getFileS3Key() {
        return fileS3Key;
    }
    
    public void setFileS3Key(String fileS3Key) {
        this.fileS3Key = fileS3Key;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public ChatTypeEnum getChatType() {
        return this.chat.getChatType();
    }
    
    public void setChatTitle(String chatTitle) {
        this.chat.setName(chatTitle);
    }
    
    public MessageStatus getStatus() {
        return status;
    }
    
    public void setStatus(MessageStatus status) {
        this.status = status;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getFileMimeType() {
        return fileMimeType;
    }
    
    public void setFileMimeType(String fileMimeType) {
        this.fileMimeType = fileMimeType;
    }
    
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getReplyId() {
        return replyId;
    }

    public void setReplyId(UUID replyId) {
        this.replyId = replyId;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }
}
