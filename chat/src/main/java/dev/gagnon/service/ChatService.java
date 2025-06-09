package dev.gagnon.service;

import dev.gagnon.dto.ChatConversationResponse;
import dev.gagnon.dto.ChatMessageRequest;
import dev.gagnon.dto.ChatMessageResponse;
import dev.gagnon.model.ChatMessage;
import dev.gagnon.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessageRequest request) {
        ChatMessage message = new ChatMessage();
        message.setSenderUsername(request.getSenderUsername());
        message.setRecipientUsername(request.getRecipientUsername());
        message.setContent(request.getContent());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessageResponse> getChatHistory(String user1, String user2) {
        return chatMessageRepository.findConversation(user1, user2).stream()
            .map(ChatMessageResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<ChatConversationResponse> getUserConversations(String username) {
        return chatMessageRepository.findUserConversations(username).stream()
            .map(ChatConversationResponse::fromEntity)
            .collect(Collectors.toList());
    }
}