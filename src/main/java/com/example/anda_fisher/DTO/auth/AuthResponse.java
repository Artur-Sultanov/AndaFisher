package com.example.anda_fisher.DTO.auth;

import com.example.anda_fisher.DTO.UserDTO;

public record AuthResponse(
        String accessToken,
        UserDTO user
) {
}
