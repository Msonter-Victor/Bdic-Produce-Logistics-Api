package dev.gagnon.repository;

import dev.gagnon.model.UserOnlineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserOnlineStatusRepository extends JpaRepository<UserOnlineStatus, String> {
    
    Optional<UserOnlineStatus> findByEmail(String email);
    
    boolean existsByEmailAndIsOnlineTrue(String email);
}