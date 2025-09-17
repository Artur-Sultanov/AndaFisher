package com.example.anda_fisher.Security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "abcdefghijklmnopqrstuvwxyz123456";

    @Test
    void generateTokenShouldEmbedUsernameAndRole() {
        JwtService jwtService = new JwtService(SECRET, 3600000);

        String token = jwtService.generateToken("testUser", "USER");

        assertThat(token).isNotBlank();
        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.getUsernameFromToken(token)).isEqualTo("testUser");
        assertThat(jwtService.extractRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    void validateTokenShouldReturnFalseForMalformedToken() {
        JwtService jwtService = new JwtService(SECRET, 3600000);

        assertThat(jwtService.validateToken("not-a-valid-token")).isFalse();
    }

    @Test
    void constructorShouldRejectShortSecret() {
        assertThatThrownBy(() -> new JwtService("short", 3600000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JWT secret must be at least 256 bits long");
    }
}
