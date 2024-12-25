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
        assertFalse(violations.isEmpty(), "Электронная почта должна быть в формате 'example@domain.com");

        // Проверка конкретного сообщения ошибки для email
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Электронная почта должна быть в формате 'example@domain.com'", errorMessage);
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
        assertFalse(violations.isEmpty(), "Логин не может содержать пробелы");

        // Проверка сообщения ошибки для логина
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Логин не может содержать пробелы", errorMessage);
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
        assertFalse(violations.isEmpty(), "Дата рождения не может быть в будущем");

        // Проверка сообщения ошибки для будущей даты
        String errorMessage = violations.iterator().next().getMessage();
        assertEquals("Дата рождения не может быть в будущем", errorMessage);
    }


    @Test
    public void testCreateMethodWithEmptyEmail() {
        // Создаем пользователя с пустым email
        User user = User.builder()
                        .email(" ")  // Пустой email
                        .login("validLogin")
                        .name("Valid Name")
                        .birthday(LocalDate.now().plusDays(1)) // Дата в будущем
                        .build();

        // Используем валидатор для проверки данных пользователя
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Проверяем, что нарушения валидации не пусты
        assertFalse(violations.isEmpty(), "Электронная почта не может быть пустой и должна содержать символ @");
    }

    @Test
    void testEmptyLogin() {
        User user = User.builder()
                        .email("test@example.com")
                        .login(" ")
                        .name("Valid Name")
                        .birthday(LocalDate.of(2000, 1, 1))
                        .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не может содержать пробелы");
    }

    @Test
    void testEmptyBirthday() {
        User user = User.builder()
                        .email("test@example.com")
                        .login("validLogin")
                        .name("Valid Name")
                        .birthday(null) // Null birthday
                        .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Дата рождения должна быть указана");
    }
}