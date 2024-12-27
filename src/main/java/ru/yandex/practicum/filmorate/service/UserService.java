package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public List<UserDto> findAll() {
        return userRepository.findAll()
                             .stream()
                             .map(UserMapper::mapToUserDto)
                             .toList();
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id)
                             .map(UserMapper::mapToUserDto)
                             .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public UserDto create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        checkName(user);
        return UserMapper.mapToUserDto(userRepository.create(user));
    }

    public UserDto update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (findById(newUser.getId()) == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", newUser.getId()));
        }
        checkName(newUser);
        return UserMapper.mapToUserDto(userRepository.update(newUser));
    }

    public List<UserDto> getFriends(Long receiver) {
        if (userRepository.findById(receiver).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", receiver));
        }
        return userRepository.getFriends(receiver)
                             .stream()
                             .map(UserMapper::mapToUserDto)
                             .toList();
    }

    public void addFriend(Long sender, Long receiver) {
        userRepository.findById(sender)
                      .orElseThrow(() -> new NotFoundException("Пользователь с id=" + sender + " не найден"));

        userRepository.findById(receiver)
                      .orElseThrow(() -> new NotFoundException("Пользователь с id=" + receiver + " не найден"));

        // Add friendship
        friendshipRepository.addFriend(sender, receiver);
    }

    public void deleteFriend(Long sender, Long receiver) {
        userRepository.findById(sender)
                      .orElseThrow(() -> new NotFoundException("Пользователь с id=" + sender + " не найден"));

        userRepository.findById(receiver)
                      .orElseThrow(() -> new NotFoundException("Пользователь с id=" + receiver + " не найден"));

        friendshipRepository.deleteFriend(sender, receiver);
    }

    public List<UserDto> getCommonFriends(Long userId, Long friendId) {
        return userRepository.getCommonFriends(userId, friendId)
                             .stream()
                             .map(UserMapper::mapToUserDto)
                             .toList();
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
