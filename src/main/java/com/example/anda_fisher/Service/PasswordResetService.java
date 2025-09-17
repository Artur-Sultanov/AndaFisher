package com.example.anda_fisher.Service;

import com.example.anda_fisher.Model.PasswordResetToken;
import com.example.anda_fisher.Model.User;
import com.example.anda_fisher.Repository.PasswordResetTokenRepository;
import com.example.anda_fisher.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Duration PASSWORD_RESET_TTL = Duration.ofHours(1);

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String generateTokenFor(User user) {
        passwordResetTokenRepository.deleteByExpiresAtBefore(Instant.now());

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(Instant.now().plus(PASSWORD_RESET_TTL));
        token.setUsed(false);
        passwordResetTokenRepository.save(token);
        return token.getToken();
    }

    @Transactional
    public void resetPassword(String tokenValue, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findById(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (token.isUsed() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expired or used reset token");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);
    }
}
