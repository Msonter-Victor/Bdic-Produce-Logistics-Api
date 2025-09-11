package dev.gagnon.Controller;
import dev.gagnon.DTO.request.AddRoleRequest;
import dev.gagnon.DTO.request.AddressRequest;
import dev.gagnon.DTO.request.RiderRegistrationRequest;
import dev.gagnon.DTO.request.UserRegistrationRequest;
import dev.gagnon.DTO.response.UserResponse;
import dev.gagnon.Model.User;
import dev.gagnon.Model.constants.Role;
import dev.gagnon.Repository.UserRepository;
import dev.gagnon.Service.UserService;
import dev.gagnon.exception.UserBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@ModelAttribute UserRegistrationRequest request) {
        try{
            var response = userService.registerUser(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @PostMapping(value = "/register-rider")
    public ResponseEntity<?> register(@ModelAttribute RiderRegistrationRequest request) {
        try{
            String response = userService.registerRider(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @PostMapping(value = "/register-logistics")
    public ResponseEntity<?> registerLogistics(@RequestBody UserRegistrationRequest request) {
        try{
            var response = userService.registerLogCompany(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            userService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email has been resent.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/logisticsAll")
    public ResponseEntity<?>getAllLogistics(){
        List<User> users = userRepository.findAll();
        List<User> buyers = users.stream()
                .filter(user -> user.getRoles().contains(Role.LOGISTICS))
                .toList();
        return ResponseEntity.ok(buyers.size());
    }

    @GetMapping("/buyersAll")
    public ResponseEntity<?>getAllBuyers(){
        List<User> users = userRepository.findAll();
        List<User> buyers = users.stream()
                .filter(user -> user.getRoles().contains(Role.BUYER))
                .filter(user -> !user.getRoles().contains(Role.ADMIN))
                .toList();
        List<UserResponse> userResponses = buyers.stream()
                .map(UserResponse::new)
                .toList();
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/adminAll")
    public ResponseEntity<?>getAllAdmins(){
        List<User> users = userRepository.findAll();
        List<User> buyers = users.stream()
                .filter(user -> user.getRoles().contains(Role.ADMIN))
                .toList();
        List<UserResponse> userResponses = buyers.stream()
                .map(UserResponse::new)
                .toList();
        return ResponseEntity.ok(userResponses);
    }


    @PostMapping("/add-role")
    public ResponseEntity<?> addRoleToAuthenticatedUser(@RequestBody AddRoleRequest request) {
        try{
            String response = userService.addRoleToUser(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (UserBaseException e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @PostMapping("/add-admin")
    public ResponseEntity<?>addAdmin(@RequestBody UserRegistrationRequest request) {
        try{
            String response = userService.registerAdmin(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/getAllBuyersCount")
    public ResponseEntity<?> getAllBuyersCount() {
        List<User> users = userRepository.findAll();
        List<User> buyers = users.stream()
                .filter(user -> user.getRoles().contains(Role.BUYER))
                .toList();
        return ResponseEntity.ok(buyers.size());
    }

    @GetMapping("/getAllVendorsCount")
    public ResponseEntity<?>getAllVendorsCount(){
        List<User> users = userRepository.findAll();
        List<User> buyers = users.stream().filter(user -> user.getRoles().contains(Role.VENDOR)).toList();
        return ResponseEntity.ok(buyers.size());
    }

    @GetMapping("/get-profile")
    public ResponseEntity<?>getUserProfile(@RequestParam String email){
        try{
            UserResponse response = userService.getProfileFor(email);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        List<User>users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
            .map(UserResponse::new)
            .toList();
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }
    @GetMapping("/allCount")
    public ResponseEntity<?> getAllUsersCount(){
        List<User>users = userRepository.findAll();
        return ResponseEntity.ok(users.size());
    }

    @PostMapping("/add-address")
    public ResponseEntity<?>addAddress(@RequestBody AddressRequest request){
        String response = userService.addAddress(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get-userAddress")
    public ResponseEntity<?> getUserAddress(@RequestParam String email){
        var response = userService.getAddress(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/update-address")
    public ResponseEntity<?>updateAddress(@RequestBody AddressRequest request){
        String response = userService.updateAddress(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/address-exists")
    public ResponseEntity<?> addressExists(@RequestParam String email){
        boolean response = userService.addressExists(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/isUserValid/{userId}")
    public boolean isUserValid(@PathVariable Long userId) {
        return userService.existsById(userId);
    }
    @GetMapping("/exists")
    public boolean isUserValid(@RequestParam String email) {
        return userService.existsByEmail(email); // returns true/false
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?>deleteUser(@RequestParam Long id){
        try{
            String response = userService.deleteUser(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (UserBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-by-email")
    public ResponseEntity<?>deleteUser(@RequestParam String email){
        try{
            String response = userService.deleteUserByEmail(email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (UserBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    @PutMapping("/disable-user")
    public ResponseEntity<?>disableUser(@RequestParam Long id){
        try{
            String response = userService.disableUser(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (UserBaseException ex){
            return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
        }
    }

    /**
     * Get user information by session ID for bot service
     * This endpoint is used by the bot service to get user context
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> getUserBySession(@PathVariable String sessionId) {
        try {
            // For now, return empty map since session-based user lookup isn't implemented
            // In a real implementation, you would:
            // 1. Look up the session in Redis/database
            // 2. Get the associated user
            // 3. Return user information
            
            Map<String, Object> response = new HashMap<>();
            // You can implement session-based user lookup here if needed
            // For now, just return empty to avoid 403 errors in bot service
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

}
