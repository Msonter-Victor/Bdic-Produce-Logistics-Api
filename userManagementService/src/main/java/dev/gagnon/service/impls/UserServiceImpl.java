package dev.gagnon.service.impls;

import dev.gagnon.data.model.*;
import dev.gagnon.data.repository.*;
import dev.gagnon.dto.request.SignUpRequest;
import dev.gagnon.dto.response.SignUpResponse;
import dev.gagnon.dto.response.UserCreateResponse;
import dev.gagnon.exception.BdicBaseException;
import dev.gagnon.exception.EmailExistsException;
import dev.gagnon.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final BuyerRepository buyerRepository;
    private final FarmerRepository farmerRepository;
    private final RiderRepository riderRepository;
    private final EventPublisher eventPublisher;

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        validate(signUpRequest);
        User user = modelMapper.map(signUpRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        mapRoleToUser(signUpRequest, user);
        UserCreateResponse userCreateResponse = modelMapper.map(user, UserCreateResponse.class);
        eventPublisher.sendCreateMessage(userCreateResponse);
        return modelMapper.map(user, SignUpResponse.class);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                ()->new BdicBaseException("User not found"));
    }

    private void mapRoleToUser(SignUpRequest signUpRequest, User user) {
        switch (signUpRequest.getRole()){
            case ADMIN -> {
                Admin admin = new Admin();
                admin.setUser(user);
                adminRepository.save(admin);
            }
            case BUYER -> {
                Buyer buyer = new Buyer();
                buyer.setUser(user);
                buyerRepository.save(buyer);
            }
            case FARMER -> {
                Farmer farmer = new Farmer();
                farmer.setUser(user);
                farmerRepository.save(farmer);
            }
            case RIDER -> {
                Rider rider = new Rider();
                rider.setUser(user);
                riderRepository.save(rider);
            }
        }
    }

    private void validate(SignUpRequest signUpRequest) {
        validateEmail(signUpRequest.getEmail());
        validatePhone(signUpRequest.getPhone());
    }

    private void validatePhone(String phone) {

    }

    private void validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailRegex))
            throw new BdicBaseException("Invalid email format");
        validateExistingEmail(email);
    }

    private void validateExistingEmail(String email) {
        boolean existsByEmail = userRepository.existsByEmail(email);
        if (existsByEmail)
            throw new EmailExistsException("user exists with email: "+email);
    }

}
