package dev.gagnon.repository;

import dev.gagnon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String username);
    List<User> findByIsOnline(Boolean isOnline);
    
    @Modifying
    @Query("UPDATE User u SET u.isOnline = :isOnline, u.lastSeen = :lastSeen WHERE u.username = :username")
    void updateUserOnlineStatus(@Param("username") String username,
                               @Param("isOnline") Boolean isOnline, 
                               @Param("lastSeen") LocalDateTime lastSeen);
}