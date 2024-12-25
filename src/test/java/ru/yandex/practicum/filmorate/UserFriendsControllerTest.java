package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserFriendsControllerTest {

    private InMemoryUserStorage inMemoryUserStorage;
    private UserService userService;
    private UserController userController;

    private User user0;
    private User userPostman;

    @BeforeEach
    public void setUp() {
        inMemoryUserStorage = new InMemoryUserStorage(); userService = new UserService(inMemoryUserStorage);
        userController = new UserController(userService);

        user0 = User.builder().id(1L).email("john.doe@mail.com").login("johndoe").name("John Doe").birthday(LocalDate.of(1985, 5, 15))
                    .build();

        userPostman = User.builder().login("nick123").name("Nick Johnson").email("nick.johnson@mail.com").birthday(
                LocalDate.of(1990, 3, 25)).build();
    }

    @Test
    public void testAddFriend() {
        userController.create(user0); userController.create(userPostman);

        userController.addFriend(user0.getId(), userPostman.getId());

        List<User> friends = userController.getFriends(user0.getId()); assertTrue(friends.contains(userPostman),
                                                                                  "Пользователь должен быть добавлен в список друзей");
    }

    @Test
    public void testRemoveFriend() {
        userController.create(user0); userController.create(userPostman);

        userController.addFriend(user0.getId(), userPostman.getId()); userController.removeFriend(user0.getId(), userPostman.getId());

        List<User> friends = userController.getFriends(user0.getId()); assertFalse(friends.contains(userPostman),
                                                                                   "Пользователь должен быть удален из списка друзей");
    }

    @Test
    public void testGetCommonFriends() {
        userController.create(user0); userController.create(userPostman);

        User userAnother = User.builder().id(3L).email("other.user@mail.com").login("otherUser").name("Other User").birthday(
                LocalDate.of(1992, 5, 1)).build(); userController.create(userAnother);

        userController.addFriend(user0.getId(), userPostman.getId()); userController.addFriend(user0.getId(), userAnother.getId());
        userController.addFriend(userPostman.getId(), userAnother.getId());

        List<User> commonFriends = userController.getMutualFriends(user0.getId(), userPostman.getId()); assertTrue(
                commonFriends.contains(userAnother), "Пользователи должны иметь общих друзей");
    }
}