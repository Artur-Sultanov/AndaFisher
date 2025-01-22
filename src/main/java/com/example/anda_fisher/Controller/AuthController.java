package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.UserRepository;
import com.example.anda_fisher.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String phone = request.get("phoneNumber");

        if (username == null || password == null || email == null || phone == null) {
            return new ResponseEntity<>("⚠️ All fields are required!", HttpStatus.BAD_REQUEST);
        }

        if (!phone.matches("\\+?[0-9]{9,15}")) {
            return new ResponseEntity<>("⚠️ Invalid phone number format. It must be 9-15 digits, optionally starting with '+'", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByUsername(username)) {
            return new ResponseEntity<>("⚠️ Username already exists!", HttpStatus.CONFLICT);
        }

        if (email != null && userRepository.existsByEmail(email)) {
            return new ResponseEntity<>("⚠️ Email already exists!", HttpStatus.CONFLICT);
        }

        if (phone != null && userRepository.existsByPhoneNumber(phone)) {
            return new ResponseEntity<>("⚠️ Phone number already exists!", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setActive(true);

        userRepository.save(user);

        return new ResponseEntity<>("✅ User registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        Map<String, String> response = new HashMap<>();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            response.put("error", "⚠️ Username and password are required!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
                response.put("error", "❌ Invalid username or password!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = optionalUser.get();

            String token = jwtService.generateToken(user.getUsername(), user.getRole());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "❗ An unexpected error occurred. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
