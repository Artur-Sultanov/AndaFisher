package com.example.anda_fisher.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @Column(unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @Column(unique = true)
    @Pattern(
            regexp = "\\+?[0-9\\-\\s\\(\\)]{9,15}",
            message = "Phone number must be valid and contain 10 to 15 digits, optionally starting with '+'"
    )
    private String phoneNumber;


    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
