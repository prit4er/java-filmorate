package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        if (users.isEmpty()) {
            log.warn("Запрос на получение всех пользователей, но хранилище пусто");
            return Collections.emptyList();  // Возвращаем пустой список, а не null
        }
        return users.values();
    }

    public User create(User user) {
        // Валидация email
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Ошибка при добавлении пользователя: неверный email {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        // Проверка на уникальность email
        Optional<User> existingUserWithEmail = findByEmail(user.getEmail());
        if (existingUserWithEmail.isPresent()) {
            log.error("Ошибка при добавлении пользователя: email уже используется {}", user.getEmail());
            throw new ValidationException("Этот email уже используется");
        }

        // Валидация логина
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Ошибка при добавлении пользователя: неверный логин {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        // Валидация даты рождения
        if (user.getBirthday() == null) {
            log.error("Ошибка при добавлении пользователя: не указана дата рождения");
            throw new ValidationException("Дата рождения должна быть указана");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка при добавлении пользователя: дата рождения в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        // Если имя не указано, берем логин
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        // Инициализация списка друзей, если он не был передан
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }

        // Присваиваем ID и добавляем пользователя в хранилище
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь с Id {}", user.getId());
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                                 .stream()
                                 .mapToLong(id -> id)
                                 .max()
                                 .orElse(0);
        return ++currentMaxId;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка при обновлении пользователя: Id не указан");
            throw new NotFoundException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.error("Ошибка при обновлении пользователя: пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        // Проверка на уникальность email
        if (newUser.getEmail() != null && !newUser.getEmail().equals(oldUser.getEmail())) {
            Optional<User> existingUserWithEmail = findByEmail(newUser.getEmail());
            if (existingUserWithEmail.isPresent()) {
                log.error("Ошибка при обновлении пользователя: email уже используется");
                throw new ValidationException("Этот email уже используется");
            }
        }

        // Обновляем только поля, которые не равны null или пустым
        if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
            oldUser.setEmail(newUser.getEmail());
            log.trace("Изменен email пользователя с Id {}", newUser.getId());
        }
        if (newUser.getLogin() != null && !newUser.getLogin().isEmpty()) {
            oldUser.setLogin(newUser.getLogin());
            log.trace("Изменен логин пользователя с Id {}", newUser.getId());
        }
        if (newUser.getName() != null && !newUser.getName().isEmpty()) {
            oldUser.setName(newUser.getName());
            log.trace("Изменено имя пользователя с Id {}", newUser.getId());
        }
        if (newUser.getBirthday() != null && !newUser.getBirthday().isAfter(LocalDate.now())) {
            oldUser.setBirthday(newUser.getBirthday());
            log.trace("Изменена дата рождения пользователя с Id {}", newUser.getId());
        }

        log.debug("Обновлен пользователь с Id {}", newUser.getId());
        return oldUser;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.error("Пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return Optional.of(user);
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();
    }

    public Optional<List<User>> getFriends(Long id) {
        if (users.isEmpty()) {
            log.error("Ошибка при получении друзей: хранилище пусто");
            return Optional.empty();
        }

        User user = users.get(id);
        if (user == null) {
            log.error("Ошибка при получении друзей: пользователь с id = {} не найден", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

        List<User> friendsList = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = users.get(friendId);
            if (friend != null) {
                friendsList.add(friend);
            }
        }

        // Возвращаем Optional, который может быть пустым, если нет друзей
        if (friendsList.isEmpty()) {
            log.warn("У пользователя с id = {} нет друзей", id);
            return Optional.empty();
        }

        return Optional.of(friendsList);
    }
}