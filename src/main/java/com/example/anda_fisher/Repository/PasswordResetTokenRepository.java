package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.PasswordResetToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    void deleteByExpiresAtBefore(Instant instant);

}
