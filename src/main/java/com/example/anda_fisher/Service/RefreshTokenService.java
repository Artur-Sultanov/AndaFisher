package com.example.anda_fisher.Service;

import com.example.anda_fisher.Model.RefreshToken;
import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    public static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken create(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plus(REFRESH_TOKEN_TTL));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken rotate(String tokenValue) {
        RefreshToken existing = getValidToken(tokenValue);
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
        return create(existing.getUser());
    }

    @Transactional
    public void revoke(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public RefreshToken getValidToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expired or revoked refresh token");
        }

        return refreshToken;
    }
}
