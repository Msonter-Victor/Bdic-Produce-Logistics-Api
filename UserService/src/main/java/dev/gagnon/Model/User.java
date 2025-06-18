package dev.gagnon.Model;
import dev.gagnon.Model.constants.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;
    private String lastName;
    @ElementCollection(fetch=EAGER)
    @Enumerated(STRING)
    private Set<Role> roles;
    private boolean isVerified;
    @OneToMany(fetch = EAGER, cascade = CascadeType.ALL)
    private List<Review> reviews;
    private String phone;
    private String nin;
    private String mediaUrl;
    @Column(nullable = false)
    private String password;
    private String verificationToken;
    private LocalDateTime tokenExpiration;

    private LocalDateTime lastVerificationSentAt;

    private String passwordResetToken;

    private LocalDateTime passwordResetExpiration;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
