package com.example.anda_fisher.Controller;

import com.example.anda_fisher.DTO.UserDTO;
import com.example.anda_fisher.DTO.auth.AuthResponse;
import com.example.anda_fisher.DTO.auth.ForgotPasswordRequest;
import com.example.anda_fisher.DTO.auth.LoginRequest;
import com.example.anda_fisher.DTO.auth.RegisterRequest;
import com.example.anda_fisher.DTO.auth.ResetPasswordRequest;
import com.example.anda_fisher.Model.RefreshToken;
import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.UserRepository;
import com.example.anda_fisher.Security.JwtService;
import com.example.anda_fisher.Service.EmailService;
import com.example.anda_fisher.Service.PasswordResetService;
import com.example.anda_fisher.Service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthController authController;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("encoded");
        user.setRole("USER");
        user.setActive(true);
    }

    @Test
    void registerUserShouldCreateNewUser() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "Password123", "+1234567890");

        given(userRepository.existsByUsername("john")).willReturn(false);
        given(userRepository.existsByEmail("john@example.com")).willReturn(false);
        given(userRepository.existsByPhoneNumber("+1234567890")).willReturn(false);
        given(passwordEncoder.encode("Password123")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Map<String, String>> response = authController.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("message", "User registered successfully");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserShouldReturnConflictWhenUsernameExists() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "Password123", null);
        given(userRepository.existsByUsername("john")).willReturn(true);

        ResponseEntity<Map<String, String>> response = authController.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "Username already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUserShouldReturnTokensWhenCredentialsValid() {
        LoginRequest request = new LoginRequest("john@example.com", "Password123");
        given(userRepository.findByEmail("john@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("Password123", "encoded")).willReturn(true);
        given(jwtService.generateToken("john", "USER")).willReturn("accessToken");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refreshTokenValue");
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(3600));
        given(refreshTokenService.create(user)).willReturn(refreshToken);

        ResponseEntity<AuthResponse> response = authController.loginUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isEqualTo("accessToken");
        assertThat(response.getBody().user()).isInstanceOf(UserDTO.class);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).contains("refreshToken=refreshTokenValue");
    }

    @Test
    void loginUserShouldReturnUnauthorizedWhenCredentialsInvalid() {
        LoginRequest request = new LoginRequest("john@example.com", "wrong");
        given(userRepository.findByEmail("john@example.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong", "encoded")).willReturn(false);

        ResponseEntity<AuthResponse> response = authController.loginUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void refreshTokenShouldIssueNewTokens() {
        RefreshToken existingToken = new RefreshToken();
        existingToken.setToken("newRefresh");
        existingToken.setUser(user);
        existingToken.setExpiresAt(Instant.now().plusSeconds(3600));

        given(refreshTokenService.rotate("oldRefresh")).willReturn(existingToken);
        given(jwtService.generateToken("john", "USER")).willReturn("newAccess");

        ResponseEntity<AuthResponse> response = authController.refreshToken("oldRefresh");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isEqualTo("newAccess");
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).contains("refreshToken=newRefresh");
    }

    @Test
    void refreshTokenShouldReturnUnauthorizedWhenCookieMissing() {
        ResponseEntity<AuthResponse> response = authController.refreshToken(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).contains("Max-Age=0");
    }

    @Test
    void refreshTokenShouldReturnUnauthorizedWhenRotationFails() {
        given(refreshTokenService.rotate("broken")).willThrow(new IllegalArgumentException("Invalid"));

        ResponseEntity<AuthResponse> response = authController.refreshToken("broken");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).contains("Max-Age=0");
    }

    @Test
    void logoutShouldRevokeTokenWhenPresent() {
        ResponseEntity<Void> response = authController.logout("refreshValue");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).contains("Max-Age=0");
        verify(refreshTokenService).revoke("refreshValue");
    }

    @Test
    void logoutShouldSkipRevocationWhenTokenMissing() {
        ResponseEntity<Void> response = authController.logout(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(refreshTokenService, never()).revoke(anyString());
    }

    @Test
    void forgotPasswordShouldSendEmailWhenUserExists() {
        given(userRepository.findByEmail("john@example.com")).willReturn(Optional.of(user));
        given(passwordResetService.generateTokenFor(user)).willReturn("reset-token");

        ResponseEntity<Map<String, String>> response = authController.forgotPassword(new ForgotPasswordRequest("john@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "sent");
        verify(emailService).sendSimpleEmail(eq("john@example.com"), eq("Password reset"), contains("reset-token"));
    }

    @Test
    void forgotPasswordShouldSilentlySucceedWhenUserMissing() {
        given(userRepository.findByEmail("missing@example.com")).willReturn(Optional.empty());

        ResponseEntity<Map<String, String>> response = authController.forgotPassword(new ForgotPasswordRequest("missing@example.com"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(passwordResetService, never()).generateTokenFor(any());
    }

    @Test
    void resetPasswordShouldReturnOkWhenTokenValid() {
        ResponseEntity<Map<String, String>> response = authController.resetPassword(new ResetPasswordRequest("token", "NewPass123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("status", "updated");
        verify(passwordResetService).resetPassword("token", "NewPass123");
    }

    @Test
    void resetPasswordShouldReturnBadRequestWhenServiceThrows() {
        doThrow(new IllegalArgumentException("Invalid reset token")).when(passwordResetService).resetPassword("bad", "NewPass123");

        ResponseEntity<Map<String, String>> response = authController.resetPassword(new ResetPasswordRequest("bad", "NewPass123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid reset token");
    }
}
