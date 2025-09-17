package com.example.anda_fisher.Controller;

import com.example.anda_fisher.DTO.AuthTokensResponse;
import com.example.anda_fisher.DTO.LoginRequest;
import com.example.anda_fisher.DTO.PasswordResetConfirmRequest;
import com.example.anda_fisher.DTO.PasswordResetRequest;
import com.example.anda_fisher.DTO.RefreshTokenRequest;
import com.example.anda_fisher.Model.PasswordResetToken;
import com.example.anda_fisher.Service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/tokens")
@RequiredArgsConstructor
public class TokenAuthController {

    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthTokensResponse tokens = tokenService.authenticate(request.username(), request.password());
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String accessToken = tokenService.createAccessTokenFromRefresh(request.refreshToken());
            return ResponseEntity.ok(Map.of("token", accessToken));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            tokenService.revokeRefreshToken(request.refreshToken());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        try {
            PasswordResetToken token = tokenService.createPasswordResetToken(request.identifier());
            return ResponseEntity.ok(Map.of(
                    "token", token.getToken(),
                    "expiresAt", token.getExpiresAt().toString()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            tokenService.resetPassword(request.token(), request.newPassword());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
