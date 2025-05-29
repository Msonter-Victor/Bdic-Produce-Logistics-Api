package dev.gagnon.service;

import dev.gagnon.model.ChatRoom;
import dev.gagnon.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ChatRoomService {
    @Autowired private ChatRoomRepository chatRoomRepository;

    public Optional<Long> getChatId(
            Long senderId, Long recipientId, boolean createIfNotExist) {

         return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                 .or(() -> {
                    if(!createIfNotExist) {
                        return Optional.empty();
                    }
                     // Generate a unique chat ID - you might want to use a different strategy
                     Long chatId = Math.abs(senderId.hashCode() * 31L + recipientId.hashCode());

                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(senderId)
                            .recipientId(recipientId)
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(recipientId)
                            .recipientId(senderId)
                            .build();
                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }
}