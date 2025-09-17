package com.example.anda_fisher.DTO;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(@NotBlank(message = "Identifier is required") String identifier) {
}
