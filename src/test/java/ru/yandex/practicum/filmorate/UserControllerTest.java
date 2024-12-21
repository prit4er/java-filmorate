package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;
    private UserController userController;

    private User user0;
    private User userPostman;

    @BeforeEach
    public void setUp() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        userController = new UserController(inMemoryUserStorage, userService);

        user0 = User.builder()
                    .id(1L)
                    .email("john.doe@mail.com")
                    .login("johndoe")
                    .name("John Doe")
                    .birthday(LocalDate.of(1985, 5, 15))
                    .build();

        userPostman = User.builder()
                          .login("nick123")
                          .name("Nick Johnson")
                          .email("nick.johnson@mail.com")
                          .birthday(LocalDate.of(1990, 3, 25))
                          .build();
    }

    @Test
    public void testFindAllWithEmptyUsers() {
        // Вместо того, чтобы ожидать исключение, проверим, что возвращается пустой список
        assertTrue(userController.findAll().isEmpty(), "Список пользователей должен быть пустым");
    }

    @Test
    public void testFindAllWithFilledUsers() {
        userController.create(user0);
        assertEquals(1, userController.findAll().size());
    }

    @Test
    public void testCreateUserWithValidData() {
        User testUserObj = userController.create(user0);
        assertNotNull(testUserObj.getId(), "ID должен быть присвоен");
        assertEquals(user0.getEmail(), testUserObj.getEmail());
    }

    @Test
    public void testCreateUserWithEmptyEmail() {
        user0.setEmail(" ");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user0));
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        user0.setLogin(" ");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user0));
        assertEquals("Логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    public void testCreateUserWithNullBirthday() {
        user0.setBirthday(null);
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user0));
        assertEquals("Дата рождения должна быть указана", exception.getMessage());
    }

    @Test
    public void testCreateUserWithBirthdayInFuture() {
        user0.setBirthday(LocalDate.of(2300, 12, 28));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user0));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    public void testUpdateMethodWithNullId() {
        user0.setId(null);  // Устанавливаем id равным null
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.update(user0));
        assertEquals("Id должен быть указан", exception.getMessage());  // Проверяем правильность сообщения
    }

    @Test
    public void testCreateUserWithSameEmail() {
        // Создаем первого пользователя
        userController.create(user0);

        // Создаем второго пользователя с таким же email
        User user1 = user0.toBuilder().id(2L).email("john.doe@mail.com").build();

        // Попытка создания второго пользователя с тем же email
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.create(user1));
        assertEquals("Этот email уже используется", exception.getMessage());
    }

    @Test
    public void testUpdateUserWithWrongId() {
        userController.create(user0);
        User user1 = user0.toBuilder().id(44L).build();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userController.update(user1));
        assertEquals("Пользователь с id = 44 не найден", exception.getMessage());
    }

    @Test
    public void testUpdateUserWithValidData() {
        userController.create(user0);
        user0.setEmail("john.new@mail.com");
        user0.setLogin("newjohndoe");
        user0.setName("John New Doe");
        user0.setBirthday(LocalDate.of(1990, 10, 10));

        User updatedUser = userController.update(user0);

        assertEquals("john.new@mail.com", updatedUser.getEmail());
        assertEquals("newjohndoe", updatedUser.getLogin());
        assertEquals("John New Doe", updatedUser.getName());
        assertEquals(LocalDate.of(1990, 10, 10), updatedUser.getBirthday());
    }
}