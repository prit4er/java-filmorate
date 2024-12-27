package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Friendship;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper, Friendship.class);
    }

    public void addFriend(Long sender, Long receiver) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM friends WHERE sender = ? AND receiver = ?",
                Integer.class, sender, receiver
        );
        if (count != null && count > 0) {
            throw new DuplicatedDataException("Запрос на добавление в друзья уже направлен");
        }

        Friendship friendship = new Friendship(null, sender, receiver);
        insert(
                "INSERT INTO friends(sender, receiver) VALUES (?, ?)",
                friendship.getSender(),
                friendship.getReceiver()
        );
    }

    public void deleteFriend(Long sender, Long receiver) {
        delete("DELETE FROM friends WHERE sender = ? AND receiver = ?", sender, receiver);
    }
}