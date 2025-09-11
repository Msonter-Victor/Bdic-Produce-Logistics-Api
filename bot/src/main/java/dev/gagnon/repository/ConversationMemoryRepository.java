package dev.gagnon.repository;

import dev.gagnon.model.ConversationMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversationMemoryRepository extends JpaRepository<ConversationMemory, Long> {
    
    List<ConversationMemory> findBySessionIdOrderByTimestampDesc(String sessionId);
    
    List<ConversationMemory> findBySessionIdAndTimestampAfterOrderByTimestampDesc(
        String sessionId, LocalDateTime after);
    
    @Query(value = """
        SELECT * FROM conversation_memory 
        WHERE session_id = :sessionId 
        ORDER BY embedding <-> CAST(:queryEmbedding AS vector) 
        LIMIT :limit
        """, nativeQuery = true)
    List<ConversationMemory> findSimilarMemories(
        @Param("sessionId") String sessionId,
        @Param("queryEmbedding") String queryEmbedding,
        @Param("limit") int limit);
    
    @Query(value = """
        SELECT * FROM conversation_memory 
        WHERE session_id = :sessionId 
        AND timestamp >= :since
        ORDER BY timestamp DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<ConversationMemory> findRecentMemories(
        @Param("sessionId") String sessionId,
        @Param("since") LocalDateTime since,
        @Param("limit") int limit);
        
    void deleteBySessionId(String sessionId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ConversationMemory c WHERE c.userEmail = :userEmail")
    int deleteByUserEmail(@Param("userEmail") String userEmail);
}