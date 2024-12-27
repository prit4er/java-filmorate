package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingRepository extends BaseRepository<MpaRating> {

    public MpaRatingRepository(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper, MpaRating.class);
    }

    public List<MpaRating> findAll() {
        return findMany("SELECT * FROM ratings");
    }

    public Optional<MpaRating> findById(Long id) {
        return findOne("SELECT * FROM ratings WHERE id = ?", id);
    }
}