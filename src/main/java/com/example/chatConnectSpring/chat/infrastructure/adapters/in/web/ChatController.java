package com.example.chatConnectSpring.chat.infrastructure.adapters.in.web;

import com.example.chatConnectSpring.chat.application.command.AddParticipantCommand;
import com.example.chatConnectSpring.chat.application.command.CreateChatCommand;
import com.example.chatConnectSpring.chat.application.mapper.ChatResponseMapper;
import com.example.chatConnectSpring.chat.application.service.ChatService;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.request.AddParticipantRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.request.CreateChatRequestDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.response.ChatParticipantResponseDTO;
import com.example.chatConnectSpring.chat.infrastructure.adapters.in.web.dto.response.ChatResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponseDTO create(@Valid @RequestBody CreateChatRequestDTO request) {
        return ChatResponseMapper.toDTO(
                chatService.create(new CreateChatCommand(
                        request.createdByUserId(),
                        request.title(),
                        request.type()
                ))
        );
    }

    @GetMapping("/{chatId}")
    public ChatResponseDTO findById(@PathVariable UUID chatId) {
        return ChatResponseMapper.toDTO(chatService.findById(chatId));
    }

    @PostMapping("/{chatId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatParticipantResponseDTO addParticipant(
            @PathVariable UUID chatId,
            @Valid @RequestBody AddParticipantRequestDTO request
    ) {
        return ChatResponseMapper.toDTO(
                chatService.addParticipant(new AddParticipantCommand(chatId, request.userId()))
        );
    }
}
