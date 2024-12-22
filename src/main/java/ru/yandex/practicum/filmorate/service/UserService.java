package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId)
                               .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        User friend = userStorage.getUserById(friendId)
                                 .orElseThrow(() -> new NotFoundException("Друг с id = " + friendId + " не найден."));

        // Проверяем, что пользователь не может быть другом сам с собой
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Невозможно добавить самого себя в друзья.");
        }

        // Добавляем друг друга в список друзей
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId)
                               .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
        User friend = userStorage.getUserById(friendId)
                                 .orElseThrow(() -> new NotFoundException("Друг с id = " + friendId + " не найден."));
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        User user = userStorage.getUserById(id)
                               .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден."));
        User otherUser = userStorage.getUserById(otherId)
                                    .orElseThrow(() -> new NotFoundException("Пользователь с id = " + otherId + " не найден."));

        return user.getFriends().stream()
                   .filter(otherUser.getFriends()::contains)
                   .map(userStorage::getUserById)
                   .flatMap(Optional::stream)
                   .toList();
    }

}