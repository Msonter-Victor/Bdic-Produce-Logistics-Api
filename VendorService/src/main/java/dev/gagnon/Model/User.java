package dev.gagnon.Model;

//public class User {
//}
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    private String surname;

    @Column(name = "other_name", nullable = false)
    private String otherName;


    private String phone;

    private String nin;

    private String passportUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    @Column(nullable = false)
    private String password;
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;
    @Column(name = "verification_token", unique = true)
    private String verificationToken;
    @Column(name = "token_expiration")
    private LocalDateTime tokenExpiration;

    @Column(name = "last_verification_sent_at")
    private LocalDateTime lastVerificationSentAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_expiration")
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
