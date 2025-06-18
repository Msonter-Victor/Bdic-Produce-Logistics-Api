package dev.gagnon.security.data.models;


import dev.gagnon.Model.Review;
import dev.gagnon.Model.User;
import dev.gagnon.Model.constants.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SecureUser implements UserDetails {
    private final User user;
    @Getter
    private final String firstName;
    @Getter
    private final String lastName;
    @Getter
    private final String mediaUrl;
    @Getter
    private final Set<Role> roles;
    @Getter
    private final boolean isVerified;

    public SecureUser(User user) {
        this.user = user;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.mediaUrl = user.getMediaUrl();
        this.roles = user.getRoles();
        this.isVerified = user.isVerified();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private int calculateRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0;
        return reviews.stream().mapToInt(Review::getRating).sum() / reviews.size();
    }

    @Override
    public String toString() {
        return "[" +
                "Username=" + user.getEmail() +
                ", Password=" + "[PROTECTED]" +
                ", Enabled=" + isEnabled() +
                ", AccountNonExpired=" + isAccountNonExpired() +
                ", CredentialsNonExpired=" + isCredentialsNonExpired() +
                ", AccountNonLocked=" + isAccountNonLocked() +
                ", Granted Authorities=" + getAuthorities() +
                ']';
    }
}
