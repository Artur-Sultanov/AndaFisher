package com.example.anda_fisher.DTO;

import com.example.anda_fisher.DTO.auth.ForgotPasswordRequest;
import com.example.anda_fisher.DTO.auth.LoginRequest;
import com.example.anda_fisher.DTO.auth.RegisterRequest;
import com.example.anda_fisher.DTO.auth.ResetPasswordRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    @Test
    void registerRequestShouldBeValidWithCorrectData() {
        RegisterRequest request = new RegisterRequest(
                "validUser",
                "valid@example.com",
                "StrongPass123",
                "+1234567890"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void registerRequestShouldFailForInvalidFields() {
        RegisterRequest request = new RegisterRequest(
                "",
                "invalid-email",
                "short",
                "123"
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations)
                .hasSizeGreaterThanOrEqualTo(3)
                .allMatch(violation -> violation.getMessage() != null && !violation.getMessage().isBlank());
    }

    @Test
    void loginRequestShouldBeValid() {
        LoginRequest request = new LoginRequest("user@example.com", "password");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void loginRequestShouldDetectBlankFields() {
        LoginRequest request = new LoginRequest("", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations)
                .hasSize(2)
                .allMatch(violation -> violation.getMessage() != null && !violation.getMessage().isBlank());
    }

    @Test
    void forgotPasswordRequestShouldRequireValidEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("invalid");

        Set<ConstraintViolation<ForgotPasswordRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void resetPasswordRequestShouldEnforceConstraints() {
        ResetPasswordRequest invalidRequest = new ResetPasswordRequest("", "short");

        Set<ConstraintViolation<ResetPasswordRequest>> violations = validator.validate(invalidRequest);

        assertThat(violations)
                .hasSize(2)
                .allMatch(violation -> violation.getMessage() != null && !violation.getMessage().isBlank());
    }
}
