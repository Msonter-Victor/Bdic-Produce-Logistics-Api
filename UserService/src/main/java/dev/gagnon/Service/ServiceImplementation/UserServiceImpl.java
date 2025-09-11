package dev.gagnon.Service.ServiceImplementation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dev.gagnon.DTO.request.*;
import dev.gagnon.DTO.response.*;
import dev.gagnon.Model.Address;
import dev.gagnon.Model.User;
import dev.gagnon.Model.VerificationToken;
import dev.gagnon.Model.constants.Role;
import dev.gagnon.Model.constants.UserStatus;
import dev.gagnon.Repository.AddressRepository;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Repository.VerificationTokenRepository;
import dev.gagnon.Service.EmailService;
import dev.gagnon.Service.OTPService;
import dev.gagnon.Service.UserService;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.UserNotFoundException;
import dev.gagnon.security.config.RsaKeyProperties;
import dev.gagnon.security.data.models.SecureUser;
import dev.gagnon.security.services.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static dev.gagnon.Model.constants.Role.ADMIN;
import static dev.gagnon.Model.constants.UserStatus.*;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final VerificationTokenRepository tokenRepository;
    private final AddressRepository addressRepository;
    private final OTPService otpService;


    @Override
    public RegisterResponse registerUser(UserRegistrationRequest request) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user exists with email");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(PENDING)
                .isVerified(false)
                .password(passwordEncoder.encode(request.getPassword()));

        User user = userBuilder.build();
        user.setRoles(new HashSet<>());
        user.getRoles().add(Role.BUYER);
        User savedUser = userRepository.save(user);
        
        // Create OTP token and send OTP email
        VerificationToken verificationToken = otpService.createOTPToken(savedUser);
        emailService.sendOTPEmail(savedUser.getEmail(), savedUser.getFirstName(), verificationToken.getOtp());
        RegisterResponse response = modelMapper.map(savedUser, RegisterResponse.class);
        response.setMessage("User registered successfully.");
        return response;
    }


    @Override
    public boolean verifyUser(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return false;
        }
        VerificationToken vToken = verificationToken.get();
        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        User user = vToken.getUser();
        user.setStatus(UserStatus.VERIFIED);
        user.setVerified(true);
        userRepository.save(user);
        tokenRepository.delete(vToken);
        emailService.sendWelcomeEmail(user.getEmail(),user.getFirstName());
        return true;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        if (user.isVerified()) {
            throw new BusinessException("This account has already been verified.");
        }
        
        // Delete existing token if any
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        // Create new verification token
        VerificationToken newToken = new VerificationToken(user);
        tokenRepository.save(newToken);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), newToken.getToken(), "https://marketplace.bdic.ng/register/emailVerification");
    }

    @Override
    public boolean verifyUserWithOTP(String email, String otp) {
        return otpService.validateOTP(email, otp);
    }

    @Override
    public boolean resendOTP(String email) {
        return otpService.resendOTP(email);
    }

    @Override
    @Transactional
    public void sendVerificationOTP(String email) {
        log.info("Attempting to send verification OTP for email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        log.info("Found user: id={}, email={}, verified={}", user.getId(), user.getEmail(), user.isVerified());
        
        if (user.isVerified()) {
            throw new BusinessException("This account has already been verified.");
        }
        
        // Create OTP token and send OTP email
        try {
            VerificationToken verificationToken = otpService.createOTPToken(user);
            log.info("Created OTP token for user: {}", user.getEmail());
            
            emailService.sendOTPEmail(user.getEmail(), user.getFirstName(), verificationToken.getOtp());
            log.info("Successfully sent verification OTP to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification OTP to {}: {}", user.getEmail(), e.getMessage(), e);
            throw new BusinessException("Failed to send verification OTP: " + e.getMessage());
        }
    }

    @Override
    public User findByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(()->new RuntimeException("User not found."));
    }

    @Override
    public UserResponse getProfileFor(String email) {
        User user =userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found."));
        return new UserResponse(user);
    }

    @Override
    public String addAddress(AddressRequest request) {
        User user =userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UserNotFoundException("User not found."));
        Address address = addressRepository.findByUser(user.getId());
        if (address != null) throw new BusinessException("Address already exists for user");
        address = new Address();
        address.setAddress(request.getAddress());
        address.setLga(request.getLga());
        address.setState(request.getState());
        address.setUser(user);
        addressRepository.save(address);
        return "Address created successfully";
    }

    @Override
    public AddressResponse getAddress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found."));
        Address address = addressRepository.findByUser(user.getId());
        return new AddressResponse(address);
    }

    @Override
    public String updateAddress(AddressRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new UserNotFoundException("User not found."));
        Address address = addressRepository.findByUser(user.getId());
        address.setAddress(request.getAddress());
        address.setLga(request.getLga());
        address.setState(request.getState());
        addressRepository.save(address);
        return "Address updated successfully";
    }

    @Override
    public boolean addressExists(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found."));
        Address address = addressRepository.findByUser(user.getId());
        return address != null;
    }



    @Override
    public boolean isUserVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.isVerified();
    }

    @Override
    public String resetPassword(PasswordRequest passwordRequest) {
        User user = userRepository.findByEmail(passwordRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        userRepository.save(user);
        return "Password reset successful";
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found."));
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        userRepository.delete(user);

        return "user deleted";
    }



    @Override
    public String disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("User not found."));
        user.setStatus(INACTIVE);
        user.setVerified(false);
        userRepository.save(user);
        return "user deleted";
    }

    @Override
    public String registerAdmin(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user exists with email");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(VERIFIED)
                .isVerified(true)
                .password(passwordEncoder.encode(request.getPassword()));
        User user = userBuilder.build();
        user.setRoles(new HashSet<>());
        user.getRoles().add(ADMIN);
        user.setVerified(true);
        userRepository.save(user);
        return "User registered successfully.";
    }

    @Override
    public String addRoleToUser(AddRoleRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        try {
            Role role = Role.valueOf(request.getRoleName().toUpperCase());
            user.getRoles().add(role);
            userRepository.save(user);
            return "Role '" + request.getRoleName() + "' added successfully to user " + request.getEmail();
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role name: " + request.getRoleName());
        }
    }

    @Override
    public boolean sendPasswordResetOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        if (!user.isVerified()) {
            throw new BusinessException("Account not verified. Please verify your account first.");
        }
        
        return otpService.resendPasswordResetOTP(email);
    }

    @Override
    public boolean verifyPasswordResetOTP(String email, String otp) {
        return otpService.validatePasswordResetOTP(email, otp);
    }

    @Override
    public boolean resetPasswordWithOTP(String email, String otp, String newPassword) {
        // First verify the OTP
        if (!otpService.validatePasswordResetOTP(email, otp)) {
            return false;
        }
        
        // Update the password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Clean up the token after successful password reset
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        return true;
    }

    @Override
    public String registerLogCompany(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user exists with email");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(VERIFIED)
                .isVerified(true)
                .password(passwordEncoder.encode(request.getPassword()));
        User user = userBuilder.build();
        user.setRoles(new HashSet<>());
        user.getRoles().add(Role.LOGISTICS);
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
        return "Company registered successfully.";
    }

    @Override
    public String registerRider(RiderRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("user exists with email");
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .status(VERIFIED)
                .isVerified(true)
                .password(passwordEncoder.encode(request.getPassword()));
        User user = userBuilder.build();
        user.setRoles(new HashSet<>());
        user.getRoles().add(Role.RIDER);
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
        return "Rider registered successfully.";
    }

    @Override
    public String deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found."));
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        userRepository.delete(user);
        return "user deleted";
    }


}
