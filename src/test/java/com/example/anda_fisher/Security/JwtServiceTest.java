package com.example.anda_fisher.Security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String TEST_SECRET = "MySuperSecretKey12345678901234567890";
    private static final long EXPIRATION_TIME = 3600000;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, EXPIRATION_TIME);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken("testUser", "USER");

        assertNotNull(token, "Generated token should not be null");
    }

    @Test
    void testValidateToken_Valid() {
        String token = jwtService.generateToken("testUser", "USER");

        boolean isValid = jwtService.validateToken(token);

        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void testValidateToken_Invalid() {
        String invalidToken = "Invalid.Token.String";

        boolean isValid = jwtService.validateToken(invalidToken);

        assertFalse(isValid, "Token should be invalid");
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtService.generateToken("testUser", "USER");

        String username = jwtService.getUsernameFromToken(token);

        assertEquals("testUser", username, "Extracted username should match the original username");
    }

    @Test
    void testExtractRole() {
        String token = jwtService.generateToken("testUser", "USER");

        String role = jwtService.extractRole(token);

        assertEquals("ROLE_USER", role, "Extracted role should match the original role");
    }

    @Test
    void testTokenExpiration() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService(TEST_SECRET, 1000);

        String token = shortLivedJwtService.generateToken("testUser", "USER");

        Thread.sleep(2000);

        boolean isValid = shortLivedJwtService.validateToken(token);

        assertFalse(isValid, "Token should be expired");
    }

    @Test
    void testInvalidSecretKey() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService("shortKey", EXPIRATION_TIME),
                "Should throw exception for short secret key");
    }
}
