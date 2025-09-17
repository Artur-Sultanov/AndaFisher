package com.example.anda_fisher.Repository;

import com.example.anda_fisher.Model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmail returns user when email exists")
    void findByEmailReturnsUserWhenEmailExists() {
        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertThat(result)
                .as("Expected to find user by email")
                .isPresent();

        User foundUser = result.orElseThrow();
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.getUsername()).isEqualTo("test_user");
    }

    @Test
    @DisplayName("findByEmail returns empty when email does not exist")
    void findByEmailReturnsEmptyWhenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("absent@example.com");

        assertThat(result)
                .as("Expected no user to be found for non-existing email")
                .isNotPresent();
    }
}
