package dev.gagnon.repository;

import dev.gagnon.model.ChatMessage;
import dev.gagnon.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    long countBySenderIdAndRecipientIdAndStatus(
            Long senderId, Long recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(Long chatId);

    @Transactional
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.status = :status WHERE cm.senderId = :senderId AND cm.recipientId = :recipientId")
    void updateStatuses(@Param("senderId") Long senderId, 
                      @Param("recipientId") Long recipientId, 
                      @Param("status") MessageStatus status);
}