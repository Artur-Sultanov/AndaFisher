package com.example.anda_fisher.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "\\+?[0-9\\-\\s\\(\\)]{9,15}",
            message = "Phone number must be valid and contain 9 to 15 digits, optionally starting with '+'"
    )
    private String phoneNumber;

    private String role; // String для удобства
    private boolean isActive;
}
