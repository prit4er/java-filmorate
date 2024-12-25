package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        validateUser(user);

        // Проверка на уникальность email
        if (isEmailAlreadyUsed(user.getEmail(), null)) {
            throw new ValidationException("Этот email уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        validateUser(newUser);

        // Находим пользователя по ID
        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден.");
        }

        // Проверка на уникальность email после нахождения пользователя
        if (isEmailAlreadyUsed(newUser.getEmail(), newUser.getId())) {
            throw new ValidationException("Этот email уже используется другим пользователем");
        }

        // Обновление полей пользователя
        mergeUserFields(existingUser, newUser);
        return existingUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден."); // Используем кастомное исключение
        }

        // Получаем список друзей по их ID
        return user.getFriends().stream()
                   .map(users::get)
                   .filter(Objects::nonNull) // Исключаем null (если пользователь-друг был удален)
                   .collect(Collectors.toList());
    }

    private long getNextId() {
        return users.isEmpty() ? 1 : Collections.max(users.keySet()) + 1;
    }

    private boolean isEmailAlreadyUsed(String email, Long userId) {
        return users.values().stream()
                    .anyMatch(user -> !user.getId().equals(userId) && user.getEmail().equals(email));
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения должна быть указана и не может быть в будущем");
        }
    }

    private void mergeUserFields(User oldUser, User newUser) {
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null && !newUser.getBirthday().isAfter(LocalDate.now())) {
            oldUser.setBirthday(newUser.getBirthday());
        }
    }
}