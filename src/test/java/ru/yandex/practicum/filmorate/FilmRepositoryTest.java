package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmRepository;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class})
class FilmRepositoryTest {

    private final FilmRepository filmRepository;

    @BeforeEach
    void cleanUp() {
        filmRepository.findAll().forEach(film -> filmRepository.delete(String.valueOf(film.getId())));
    }

    @Test
    void create_ValidFilm_SavesFilm() {
        Film film = createFilm("New Film", "New Description", 150, 1L, "G", LocalDate.of(2023, 3, 1));

        Film savedFilm = filmRepository.create(film);

        assertThat(savedFilm.getId()).isNotNull();
        assertThat(filmRepository.findById(savedFilm.getId())).isPresent();
    }

    @Test
    void findAll_ReturnsListOfFilms() {
        Film film1 = createFilm("Film One", "Description One", 120, 1L, "G", LocalDate.of(2023, 1, 1));
        Film film2 = createFilm("Film Two", "Description Two", 90, 2L, "PG", LocalDate.of(2023, 2, 1));

        filmRepository.create(film1);
        filmRepository.create(film2);

        List<Film> films = filmRepository.findAll();

        assertThat(films).hasSize(2);
        assertThat(films).extracting(Film::getName).containsExactlyInAnyOrder("Film One", "Film Two");
    }

    // Утилитарный метод для создания тестовых фильмов
    private Film createFilm(String name, String description, int duration, Long ratingId, String ratingName, LocalDate releaseDate) {
        return new Film(
                null,
                name,
                description,
                releaseDate,
                duration,
                Set.of(),
                new MpaRating(ratingId, ratingName),
                Set.of()
        );
    }
}