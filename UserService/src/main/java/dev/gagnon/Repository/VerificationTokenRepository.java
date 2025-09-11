package dev.gagnon.Repository;

import dev.gagnon.Model.User;
import dev.gagnon.Model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByUser(User user);
    
    @Modifying
    @Query("DELETE FROM VerificationToken v WHERE v.user = :user")
    void deleteByUser(@Param("user") User user);
}
