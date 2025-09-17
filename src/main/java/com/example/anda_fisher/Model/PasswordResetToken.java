package com.example.anda_fisher.Model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;


import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")

@Getter
@Setter
public class PasswordResetToken {

    @Id
    @Column(length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used = false;
}
