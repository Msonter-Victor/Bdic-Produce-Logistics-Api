package dev.gagnon.Service.ServiceImplementation;

import dev.gagnon.DTO.UserRegistrationRequest;
import dev.gagnon.Model.Role;
import dev.gagnon.Model.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserServiceImpl {

    @Override
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use.");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .surname(request.getSurname())
                .otherName(request.getOtherName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone() != null ? request.getPhone() : null)
                .nin(request.getNin() != null ? request.getNin() : null)
                .passportUrl(request.getPassportUrl() != null ? request.getPassportUrl() : null)
                .verificationToken(UUID.randomUUID().toString())
                .tokenExpiration(LocalDateTime.now().plusMinutes(15))
                .isVerified(false);

        // Set default role (ID=1) if exists
        Role defaultRole = roleRepository.findById(1L).orElse(null);
        if (defaultRole != null) {
            userBuilder.roles(Set.of(defaultRole));
        }

        User user = userBuilder.build();
        User savedUser = userRepository.save(user);

        // Send email with verification link
        String verificationLink = "http://localhost:8982/api/users/verify?token=" + savedUser.getVerificationToken();
        emailService.sendVerificationEmail(
                savedUser.getEmail(),
                "Verify Your Account",
                "<p>Click the link to verify your account: <a href=\"" + verificationLink + "\">Verify</a></p>"
        );

        return savedUser;
    }

    @Override
    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Verification failed: Invalid verification token.");
        }

        User user = userOpt.get();

        if (user.isVerified()) {
            throw new RuntimeException("Account already verified.");
        }

        if (user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            resendVerificationEmail(user.getEmail()); // 📨 Automatically resend
            throw new RuntimeException("Verification token expired. A new email has been sent to " + user.getEmail());
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiration(null);
        userRepository.save(user);

        return true;
    }

}
