package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    public List<Film> findAll() {
        return findMany(
                "SELECT f.*, r.name AS rating_name " +
                        "FROM films f " +
                        "LEFT JOIN ratings r ON f.rating_id = r.id "
        );
    }

    public Optional<Film> findById(Long id) {
        return findOne(
                "SELECT f.*, r.name AS rating_name " +
                        "FROM films f " +
                        "LEFT JOIN ratings r ON f.rating_id = r.id " +
                        "WHERE f.id = ?",
                id
        );
    }

    public Film create(Film film) {

        long filmId = insert(
                "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                        "VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId()
        );
        film.setId(filmId);
        saveGenres(film);
        return findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден после создания"));
    }

    public Film update(Film film) {

        update(
                "UPDATE films " +
                        "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                        "WHERE id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId()
        );
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        return findById(film.getId()).orElseThrow(() -> new NotFoundException("Фильм не найден после обновления"));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        for (Genre genre : film.getGenres()) {
            jdbc.update(
                    "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)",
                    film.getId(),
                    genre.getId()
            );
        }
    }
}