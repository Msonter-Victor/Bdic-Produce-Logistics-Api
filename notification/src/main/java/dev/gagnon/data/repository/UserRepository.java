package dev.gagnon.data.repository;

import dev.gagnon.data.model.User;
import dev.gagnon.dto.request.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
