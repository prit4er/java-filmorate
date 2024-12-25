package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Работа через сервис

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на добавление в друзья пользователя с id = {} другу с id = {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос на удаление из друзей пользователя с id = {} друга с id = {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос на получение списка друзей пользователя с id = {}", id);
        List<User> friends = userService.getFriends(id);
        if (friends.isEmpty()) {
            log.warn("У пользователя с id = {} нет друзей", id);
            return Collections.emptyList();
        }
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос на получение общих друзей пользователей с id = {} и id = {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя с id = {}", userId);
        return userService.getUserById(userId)
                          .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
    }
}