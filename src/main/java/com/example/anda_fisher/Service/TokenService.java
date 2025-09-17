package com.example.anda_fisher.Service;

import com.example.anda_fisher.DTO.AuthTokensResponse;
import com.example.anda_fisher.Model.PasswordResetToken;
import com.example.anda_fisher.Model.RefreshToken;
import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.PasswordResetTokenRepository;
import com.example.anda_fisher.Repository.RefreshTokenRepository;
import com.example.anda_fisher.Repository.UserRepository;
import com.example.anda_fisher.Security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.security.refresh-token.expiration-hours:720}")
    private long refreshTokenExpirationHours;

    @Value("${app.security.password-reset.expiration-minutes:30}")
    private long passwordResetExpirationMinutes;

    public AuthTokensResponse authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        RefreshToken refreshToken = issueRefreshToken(user);
        String accessToken = jwtService.generateToken(user.getUsername(), user.getRole());

        return new AuthTokensResponse(accessToken, refreshToken.getToken(), refreshToken.getExpiresAt());
    }

    public RefreshToken issueRefreshToken(User user) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);
        activeTokens.forEach(token -> token.setRevoked(true));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plus(refreshTokenExpirationHours, ChronoUnit.HOURS));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public String createAccessTokenFromRefresh(String refreshTokenValue) {
        RefreshToken refreshToken = requireValidRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();
        return jwtService.generateToken(user.getUsername(), user.getRole());
    }

    public void revokeRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));
        refreshToken.setRevoked(true);
    }

    public PasswordResetToken createPasswordResetToken(String identifier) {
        User user = resolveUser(identifier);
        List<PasswordResetToken> existingTokens = passwordResetTokenRepository.findAllByUserAndUsedFalse(user);
        existingTokens.forEach(token -> token.setUsed(true));

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plus(passwordResetExpirationMinutes, ChronoUnit.MINUTES));
        token.setUsed(false);

        return passwordResetTokenRepository.save(token);
    }

    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken passwordResetToken = requireValidPasswordResetToken(tokenValue);
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        passwordResetToken.setUsed(true);
    }

    private RefreshToken requireValidRefreshToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        return refreshToken;
    }

    private PasswordResetToken requireValidPasswordResetToken(String tokenValue) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Password reset token not found"));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            token.setUsed(true);
            throw new IllegalArgumentException("Password reset token is expired or already used");
        }

        return token;
    }

    private User resolveUser(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
