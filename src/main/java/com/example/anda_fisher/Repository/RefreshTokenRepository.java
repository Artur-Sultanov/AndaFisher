package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.example.anda_fisher.Model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    void deleteByExpiresAtBefore(Instant instant);
}
