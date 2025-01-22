package com.example.anda_fisher.Mapper;

import com.example.anda_fisher.DTO.UserDTO;
import com.example.anda_fisher.Model.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(), // Преобразуем строку роли напрямую
                user.isActive()

        );
    }

    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "USER"); // Устанавливаем роль по умолчанию
        user.setActive(userDTO.isActive());
        return user;
    }
}
