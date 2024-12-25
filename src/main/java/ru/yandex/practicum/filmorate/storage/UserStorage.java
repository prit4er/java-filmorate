package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Optional<User> getUserById(Long id); // Возвращает Optional<User>

    List<User> getFriends(Long userId); // Добавлено
}