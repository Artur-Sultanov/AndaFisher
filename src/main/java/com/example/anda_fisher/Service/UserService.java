package com.example.anda_fisher.Service;

import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank() ||
                user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username and password must not be empty.");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("User already exists.");
        }

        if (user.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User authenticateUser(String username, String rawPassword) {
        User user = findByUsername(username);
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

}
