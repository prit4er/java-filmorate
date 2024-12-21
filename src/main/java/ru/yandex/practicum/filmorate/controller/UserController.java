package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.userService = userService;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @GetMapping
    public Collection<User> findAll() {
        return inMemoryUserStorage.findAll();
    }


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        Optional<List<User>> friendsList = inMemoryUserStorage.getFriends(id);
        if (friendsList.isPresent()) {
            return friendsList.get();
        } else return Collections.emptyList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable Long userId) {
        Optional<User> user = inMemoryUserStorage.getUserById(userId);
        if (user.isPresent()) {
            return user.get();
        } else throw new NotFoundException("Юзер с " + userId + " отсутствует.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        return Map.of("errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("errorMessage", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("errorMessage", e.getMessage());
    }
}
