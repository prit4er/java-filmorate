package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidUser() {
        User user = User.builder()
                        .email("test@example.com")
                        .login("validLogin")
                        .name("Valid Name")
                        .birthday(LocalDate.of(2000, 1, 1))
                        .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "User should be valid");
    }

    @Test
    void testInvalidEmail() {
        User user = User.builder()
                        .email("invalid-email")
                        .login("validLogin")
                        .name("Valid Name")
                        .birthday(LocalDate.of(2000, 1, 1))
                        .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User with invalid email should not be valid");
    }

    @Test
    void testLoginContainsSpaces() {
        User user = User.builder()
                        .email("test@example.com")
                        .login("invalid login with spaces")
                        .name("Valid Name")
                        .birthday(LocalDate.of(2000, 1, 1))
                        .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User with login containing spaces should not be valid");
    }

    @Test
    void testFutureBirthday() {
        User user = User.builder()
                        .email("test@example.com")
                        .login("validLogin")
                        .name("Valid Name")
                        .birthday(LocalDate.now().plusDays(1)) // Future date
                        .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "User with future birthday should not be valid");
    }
}
