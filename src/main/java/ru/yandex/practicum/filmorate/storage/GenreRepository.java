package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public List<Genre> findAll() {
        return findMany("SELECT * FROM genres");
    }

    public Optional<Genre> findById(Long id) {
        return findOne("SELECT * FROM genres WHERE id = ?", id);
    }

    public Map<Long, Set<Genre>> findGenresForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = filmIds.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining(", "));
        String query = "SELECT fg.id, fg.film_id, g.id AS genre_id, g.name AS genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (" + inClause + ") " +
                "ORDER BY fg.film_id, g.id";

        Map<Long, Set<Genre>> genresByFilm = new HashMap<>();
        jdbc.query(query, rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            genresByFilm.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        });
        return genresByFilm;
    }
}