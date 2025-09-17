package com.example.anda_fisher.DTO;

import java.time.Instant;

public record AuthTokensResponse(String accessToken, String refreshToken, Instant refreshTokenExpiresAt) {
}
