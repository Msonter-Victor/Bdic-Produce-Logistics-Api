package dev.gagnon.repository;

import dev.gagnon.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // fetch all messages between two users (both directions)
    List<ChatMessage> findBySenderIdAndRecipientIdOrSenderIdAndRecipientIdOrderByTimestamp(
        String sender1, String recipient1, String sender2, String recipient2
    );
}
