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

    private final String usersPath = "/users";
    private final String usersIdPath = "/users/{id}";
    private final String friendsIdPath = "/users/{id}/friends/{friend-id}";
    private final String friendsPath = "/users/{id}/friends";
    private final String commonFriendsPath = "/users/{id}/friends/common/{other-id}";

    private final UserService userService;

    @GetMapping(usersPath)
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping(usersIdPath)
    public UserDto findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping(usersPath)
    public UserDto create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping(usersPath)
    public UserDto update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping(friendsIdPath)
    public void addFriend(@PathVariable Long id, @PathVariable("friend-id") Long friendId) {
        userService.addFriend(friendId, id);
    }

    @DeleteMapping(friendsIdPath)
    public void deleteFriend(@PathVariable Long id, @PathVariable("friend-id") Long friendId) {
        userService.deleteFriend(friendId, id);
    }

    @GetMapping(friendsPath)
    public List<UserDto> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(commonFriendsPath)
    public List<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable("other-id") Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}