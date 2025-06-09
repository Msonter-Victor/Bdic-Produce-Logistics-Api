package dev.gagnon.repository;

import dev.gagnon.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT m FROM ChatMessage m " +
           "WHERE (m.senderUsername = :user1 AND m.recipientUsername = :user2) " +
           "OR (m.senderUsername = :user2 AND m.recipientUsername = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(@Param("user1") String user1, @Param("user2") String user2);
    
    @Query("SELECT DISTINCT " +
           "CASE WHEN m.senderUsername = :username THEN m.recipientUsername ELSE m.senderUsername END as otherUser, " +
           "m.content as lastMessage, " +
           "MAX(m.timestamp) as lastMessageTime " +
           "FROM ChatMessage m " +
           "WHERE m.senderUsername = :username OR m.recipientUsername = :username " +
           "GROUP BY otherUser " +
           "ORDER BY lastMessageTime DESC")
    List<Object[]> findUserConversations(@Param("username") String username);
}