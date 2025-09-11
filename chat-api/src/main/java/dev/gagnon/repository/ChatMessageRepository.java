package dev.gagnon.repository;

import dev.gagnon.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByConversationIdOrderByTimestampAsc(String conversationId);
    @Query("SELECT DISTINCT c.conversationId FROM ChatMessage c WHERE c.fromEmail = :email OR c.toEmail = :email")
    List<String> findConversationIdsByUserEmail(@Param("email") String email);
    @Query("SELECT c FROM ChatMessage c WHERE c.conversationId = :conversationId ORDER BY c.timestamp DESC")
    List<ChatMessage> findByConversationIdOrderByTimestampDesc(@Param("conversationId") String conversationId);
    @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.conversationId = :conversationId AND c.toEmail = :toEmail AND c.isRead = false")
    int countUnreadMessagesByConversationAndUser(@Param("conversationId") String conversationId, @Param("toEmail") String toEmail);
    @Modifying
    @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.conversationId = :conversationId AND c.toEmail = :toEmail AND c.isRead = false")
    void markMessagesAsReadByConversationAndUser(@Param("conversationId") String conversationId, @Param("toEmail") String toEmail);
    @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.toEmail = :email AND c.isRead = false")
    int countUnreadMessagesByUser(@Param("email") String email);

}