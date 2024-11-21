package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    UserController userController = new UserController();

    User user0 = User.builder()
                     .id(1L)
                     .email("john.doe@mail.com")
                     .login("johndoe")
                     .name("John Doe")
                     .birthday(LocalDate.of(1985, 5, 15))
                     .build();

    User userPostman = User.builder()
                           .login("nick123")
                           .name("Nick Johnson")
                           .email("nick.johnson@mail.com")
                           .birthday(LocalDate.of(1990, 3, 25))
                           .build();

    @Test
    public void testFindAllMethodWithEmptyUsersMap() {
        try {
            userController.findAll();
        } catch (NotFoundException e) {
            assertEquals("Список юзеров пуст", e.getMessage());
        }
    }

    @Test
    public void testFindAllMethodWithFilledUsersMap() {
        userController.create(user0);
        assertEquals(1, userController.findAll().size());
    }

    @Test
    public void testCreateMethodWithValidObject() {
        User testUserObj = userController.create(user0);
        assertEquals(user0, testUserObj);
    }

    @Test
    public void testCreateMethodWithValidObjectForPostman() {
        User testUserObj = userController.create(userPostman);
        assertEquals(userPostman, testUserObj);
    }

    @Test
    public void testCreateMethodWithEmptyEmail() {
        user0.setEmail(" ");
        try {
            userController.create(user0);
        } catch (ValidationException e) {
            assertEquals("Электронная почта не может быть пустой и должна содержать символ @", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithEmptyLogin() {
        user0.setLogin(" ");
        try {
            userController.create(user0);
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithEmptyBirthday() {
        user0.setBirthday(null);
        try {
            userController.create(user0);
        } catch (ValidationException e) {
            assertEquals("Дата рождения должна быть указана", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithBirthdayInFuture() {
        user0.setBirthday(LocalDate.of(2300, 12, 28));
        try {
            userController.create(user0);
        } catch (ValidationException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithNullId() {
        user0.setId(null);
        try {
            userController.update(user0);
        } catch (NotFoundException e) {
            assertEquals("Юзер с id = null не найден", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithSameEmail() {
        userController.create(user0);
        User user1 = user0.toBuilder().id(2L).email("john.doe@mail.com").build(); // Same email
        try {
            userController.create(user1);
            userController.update(user1);
        } catch (ValidationException e) {
            assertEquals("Этот имейл уже используется", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithWrongId() {
        userController.create(user0);
        User user1 = user0.toBuilder().id(44L).build();
        try {
            userController.update(user1);
        } catch (NotFoundException e) {
            assertEquals("Юзер с id = 44 не найден", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithValidRequest() {
        userController.create(user0);
        user0.setEmail("john.new@mail.com");
        user0.setLogin("newjohndoe");
        user0.setName("John New Doe");
        user0.setBirthday(LocalDate.of(1990, 10, 10));
        userController.update(user0);
        assertEquals("john.new@mail.com", user0.getEmail());
        assertEquals("newjohndoe", user0.getLogin());
        assertEquals("John New Doe", user0.getName());
        assertEquals(LocalDate.of(1990, 10, 10), user0.getBirthday());
    }
}