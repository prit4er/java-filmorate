package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        if (users.isEmpty()) {
            log.error("Список пользователей пуст");
            return null;
        } else return users.values();
    }


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Добавлен юзер с Id {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Юзер с id = " + newUser.getId() + " не найден");
        }

        users.put(newUser.getId(), newUser);
        log.info("Обновлен юзер: {}", newUser);
        return newUser;
    }

    // Генерация ID
    private long getNextId() {
        return users.keySet()
                    .stream()
                    .mapToLong(id -> id)
                    .max()
                    .orElse(0) + 1;
    }
}
