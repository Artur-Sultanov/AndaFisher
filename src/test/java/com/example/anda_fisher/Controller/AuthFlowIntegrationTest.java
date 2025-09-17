package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Repository.PasswordResetTokenRepository;
import com.example.anda_fisher.Model.PasswordResetToken;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void shouldCompleteFullAuthenticationLifecycle() {
        Map<String, Object> registerPayload = new HashMap<>();
        registerPayload.put("username", "integrationUser");
        registerPayload.put("email", "integration@example.com");
        registerPayload.put("password", "StrongPass123");
        registerPayload.put("phoneNumber", "+1234567890");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("message", equalTo("User registered successfully"));

        Map<String, Object> loginPayload = Map.of(
                "email", "integration@example.com",
                "password", "StrongPass123"
        );

        Response loginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginPayload)
                .when()
                .post("/api/auth/login");

        loginResponse.then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("user.username", equalTo("integrationUser"));

        String accessToken = loginResponse.jsonPath().getString("accessToken");
        String refreshToken = loginResponse.getCookie("refreshToken");

        assertThat(accessToken).isNotBlank();
        assertThat(refreshToken).isNotBlank();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(Map.of("name", "Trout"))
                .when()
                .post("/api/fish")
                .then()
                .statusCode(200)
                .body(equalTo("Fish created and pending approval."));

        Response refreshResponse = RestAssured.given()
                .cookie("refreshToken", refreshToken)
                .when()
                .post("/api/auth/refresh");

        refreshResponse.then()
                .statusCode(200)
                .body("accessToken", notNullValue());

        String rotatedAccessToken = refreshResponse.jsonPath().getString("accessToken");
        String rotatedRefreshToken = refreshResponse.getCookie("refreshToken");

        assertThat(rotatedAccessToken).isNotBlank();
        assertThat(rotatedRefreshToken).isNotBlank();
        assertThat(rotatedRefreshToken).isNotEqualTo(refreshToken);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + rotatedAccessToken)
                .body(Map.of("name", "Salmon"))
                .when()
                .post("/api/fish")
                .then()
                .statusCode(200)
                .body(equalTo("Fish created and pending approval."));

        RestAssured.given()
                .cookie("refreshToken", rotatedRefreshToken)
                .when()
                .post("/api/auth/logout")
                .then()
                .statusCode(204);

        RestAssured.given()
                .cookie("refreshToken", rotatedRefreshToken)
                .when()
                .post("/api/auth/refresh")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldResetPasswordAndLoginWithNewCredentials() {
        passwordResetTokenRepository.deleteAll();

        Map<String, Object> registerPayload = new HashMap<>();
        registerPayload.put("username", "resetUser");
        registerPayload.put("email", "reset@example.com");
        registerPayload.put("password", "InitialPass123");
        registerPayload.put("phoneNumber", "+1987654321");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registerPayload)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "reset@example.com"))
                .when()
                .post("/api/auth/forgot-password")
                .then()
                .statusCode(200)
                .body("status", equalTo("sent"));

        PasswordResetToken token = passwordResetTokenRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "token", token.getToken(),
                        "password", "NewPass456"
                ))
                .when()
                .post("/api/auth/reset-password")
                .then()
                .statusCode(200)
                .body("status", equalTo("updated"));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "email", "reset@example.com",
                        "password", "NewPass456"
                ))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }
}
