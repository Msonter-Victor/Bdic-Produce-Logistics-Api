package dev.gagnon.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_status")
public class UserStatus {
    @Id
    private Long userId;

    private boolean online;

    @Column(name = "last_seen")
    private String lastSeen;
}