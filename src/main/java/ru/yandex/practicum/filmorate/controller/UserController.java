package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    public static final String USERS_PATH = "/users";
    public static final String USERS_ID_PATH = "/users/{id}";
    public static final String FRIENDS_ID_PATH = "/users/{id}/friends/{friend-id}";
    public static final String FRIENDS_PATH = "/users/{id}/friends";
    public static final String COMMON_FRIENDS_PATH = "/users/{id}/friends/common/{other-id}";

    private final UserService userService;

    @GetMapping(USERS_PATH)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping(USERS_ID_PATH)
    public UserDto findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping(USERS_PATH)
    public UserDto create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(USERS_PATH)
    public UserDto update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping(FRIENDS_ID_PATH)
    public void addFriend(@PathVariable Long id, @PathVariable("friend-id") Long friendId) {
        userService.addFriend(friendId, id);
    }

    @DeleteMapping(FRIENDS_ID_PATH)
    public void deleteFriend(@PathVariable Long id, @PathVariable("friend-id") Long friendId) {
        userService.deleteFriend(friendId, id);
    }

    @GetMapping(FRIENDS_PATH)
    public List<UserDto> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(COMMON_FRIENDS_PATH)
    public List<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable("other-id") Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}