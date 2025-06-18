package dev.gagnon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.buyerUsername = :username OR cr.sellerUsername = :username")
    List<ChatRoom> findByParticipantUsername(@Param("username") String username);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE " +
           "(cr.buyerUsername = :user1 AND cr.sellerUsername = :user2) OR " +
           "(cr.buyerUsername = :user2 AND cr.sellerUsername = :user1) " +
           "AND cr.productId = :productId")
    Optional<ChatRoom> findByParticipantsAndProduct(
        @Param("user1") String user1, 
        @Param("user2") String user2, 
        @Param("productId") Long productId
    );
}