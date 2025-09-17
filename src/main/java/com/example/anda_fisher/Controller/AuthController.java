package com.example.anda_fisher.Controller;

import com.example.anda_fisher.DTO.UserDTO;
import com.example.anda_fisher.DTO.auth.*;
import com.example.anda_fisher.Mapper.UserMapper;
import com.example.anda_fisher.Model.RefreshToken;
import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.UserRepository;
import com.example.anda_fisher.Security.JwtService;
import com.example.anda_fisher.Service.EmailService;
import com.example.anda_fisher.Service.PasswordResetService;
import com.example.anda_fisher.Service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refreshToken";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest request) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByUsername(request.username())) {
            response.put("error", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (userRepository.existsByEmail(request.email())) {
            response.put("error", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (request.phoneNumber() != null && !request.phoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(request.phoneNumber())) {
            response.put("error", "Phone number already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhoneNumber(request.phoneNumber());
        user.setActive(true);

        userRepository.save(user);

        response.put("message", "User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.username());

        if (optionalUser.isEmpty() || !passwordEncoder.matches(request.password(), optionalUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = optionalUser.get();
        String accessToken = jwtService.generateToken(user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.create(user);

        return buildAuthResponse(user, accessToken, refreshToken.getToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return unauthorizedWithClearedCookie();
        }

        try {
            RefreshToken newRefreshToken = refreshTokenService.rotate(refreshTokenValue);
            User user = newRefreshToken.getUser();
            String accessToken = jwtService.generateToken(user.getUsername(), user.getRole());
            return buildAuthResponse(user, accessToken, newRefreshToken.getToken());
        } catch (IllegalArgumentException ex) {
            return unauthorizedWithClearedCookie();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = REFRESH_COOKIE_NAME, required = false) String refreshTokenValue) {
        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            refreshTokenService.revoke(refreshTokenValue);
        }

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, buildExpiredCookie().toString())
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            String token = passwordResetService.generateTokenFor(user);
            String message = "Use the following token to reset your password: " + token;
            emailService.sendSimpleEmail(user.getEmail(), "Password reset", message);
        });

        return ResponseEntity.ok(Map.of("status", "sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.token(), request.password());
            return ResponseEntity.ok(Map.of("status", "updated"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    private ResponseEntity<AuthResponse> buildAuthResponse(User user, String accessToken, String refreshTokenValue) {
        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshTokenValue)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(RefreshTokenService.REFRESH_TOKEN_TTL)
                .build();

        UserDTO userDTO = UserMapper.toDTO(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new AuthResponse(accessToken, userDTO));
    }

    private ResponseCookie buildExpiredCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();
    }

    private ResponseEntity<AuthResponse> unauthorizedWithClearedCookie() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, buildExpiredCookie().toString())
                .build();
    }
}
