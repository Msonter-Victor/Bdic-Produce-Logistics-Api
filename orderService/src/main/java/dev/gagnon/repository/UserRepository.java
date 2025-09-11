package dev.gagnon.repository;

import dev.gagnon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository
extends JpaRepository<User, Long> {
    @Query(value="select u from User u where u.email=:email")
    public Optional<User> findByEmail(String email);
}

