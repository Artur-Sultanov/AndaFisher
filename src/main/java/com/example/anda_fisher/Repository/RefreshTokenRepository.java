package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByExpiresAtBefore(Instant instant);
}
