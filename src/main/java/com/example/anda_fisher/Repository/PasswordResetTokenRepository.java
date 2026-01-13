package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.PasswordResetToken;
import com.example.anda_fisher.Model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    List<PasswordResetToken> findAllByUserAndUsedFalse(User user);

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByExpiresAtBefore(Instant instant);

}
