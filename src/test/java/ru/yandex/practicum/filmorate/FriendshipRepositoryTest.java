package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;
import ru.yandex.practicum.filmorate.storage.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FriendshipRepository.class, FriendshipRowMapper.class, UserRepository.class, UserRowMapper.class})
class FriendshipRepositoryTest {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    private Long userId1;
    private Long userId2;

    @BeforeEach
    void setUp() {
        userId1 = createUser("user1@example.com", "User One", "userone", LocalDate.of(2000, 1, 1)).getId();
        userId2 = createUser("user2@example.com", "User Two", "usertwo", LocalDate.of(1995, 5, 15)).getId();
    }

    @Test
    void shouldAddFriendSuccessfully() {
        friendshipRepository.addFriend(userId1, userId2);

        List<User> friends = userRepository.getFriends(userId2);

        assertThat(friends)
                .hasSize(1)
                .anyMatch(friend -> friend.getId().equals(userId1));
    }

    @Test
    void shouldDeleteFriendSuccessfully() {
        friendshipRepository.addFriend(userId1, userId2);
        friendshipRepository.deleteFriend(userId1, userId2);

        List<User> friends = userRepository.getFriends(userId2);

        assertThat(friends).isEmpty();
    }

    private User createUser(String email, String name, String login, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setLogin(login);
        user.setBirthday(birthday);
        return userRepository.create(user);
    }
}