package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    public List<User> findAll() {
        return findMany("SELECT * FROM users");
    }

    public Optional<User> findById(Long id) {
        return findOne("SELECT * FROM users WHERE id = ?", id);
    }

    public User create(User user) {
        checkEmail(user);
        long id = insert(
                "INSERT INTO users(email, name, login, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                Date.valueOf(user.getBirthday())
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        checkEmail(user);
        update(
                "UPDATE users SET email = ?, name = ?, login = ?, birthday = ? WHERE id = ?",
                user.getEmail(),
                user.getName(),
                user.getLogin(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    public List<User> getFriends(Long receiver) {
        return findMany(
                "SELECT u.* FROM users u JOIN friends f ON u.id = f.sender WHERE f.receiver = ?",
                receiver
        );
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        return jdbc.query(
                "SELECT u.* FROM users u " +
                        "JOIN friends f1 ON u.id = f1.sender " +
                        "JOIN friends f2 ON u.id = f2.sender " +
                        "WHERE f1.receiver = ? AND f2.receiver = ?",
                mapper, userId, friendId
        );
    }

    private void checkEmail(User user) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class, user.getEmail()
        );
        if (count != null && count > 0) {
            throw new DuplicatedDataException(
                    String.format("Этот email уже используется: %s", user.getEmail())
            );
        }
    }
}