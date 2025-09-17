package com.example.anda_fisher.DTO;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirmRequest(
        @NotBlank(message = "Token is required") String token,
        @NotBlank(message = "New password is required") String newPassword
) {
}
