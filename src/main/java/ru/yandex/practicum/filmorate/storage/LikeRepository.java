package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Repository
public class LikeRepository extends BaseRepository<Like> {

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper, Like.class);
    }

    public void addLike(Long filmId, Long userId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId, userId
        );
        if (count != 0) {
            throw new DuplicatedDataException(
                    String.format("Фильму с id = %d, уже поставлен лайк пользователем с id = %d", filmId, userId)
            );
        }
        insert(
                "INSERT INTO likes(film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    public void deleteLike(Long filmId, Long userId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId, userId
        );
        if (count == 0) {
            throw new DuplicatedDataException(
                    String.format("Фильму с id = %d, еще не поставлен лайк пользователем с id = %d", filmId, userId)
            );
        }
        delete("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    public List<Like> findLikesByFilmId(Long filmId) {
        return findMany("SELECT * FROM likes WHERE film_id = ?", filmId);
    }
}