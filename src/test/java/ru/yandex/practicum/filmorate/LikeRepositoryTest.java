package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmRepository;
import ru.yandex.practicum.filmorate.storage.LikeRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeRepository.class, LikeRowMapper.class, FilmRepository.class, FilmRowMapper.class, UserRepository.class, UserRowMapper.class})
class LikeRepositoryTest {

    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    private Long filmId;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = createTestUser("testuser@example.com", "Test User", "testlogin", LocalDate.of(2000, 1, 1)).getId();
        filmId = createTestFilm("Test Film", "Test Film Description", LocalDate.of(2023, 1, 1), 120).getId();
    }

    @Test
    void shouldAddLikeToFilm() {
        likeRepository.addLike(filmId, userId);

        List<Like> likes = likeRepository.findLikesByFilmId(filmId);
        assertThat(likes).anyMatch(like -> like.getUserId().equals(userId));
    }

    @Test
    void shouldDeleteLikeFromFilm() {
        likeRepository.addLike(filmId, userId);

        // Act
        likeRepository.deleteLike(filmId, userId);

        List<Like> likes = likeRepository.findLikesByFilmId(filmId);
        assertThat(likes).noneMatch(like -> like.getUserId().equals(userId));
    }

    @Test
    void shouldFindLikesByFilmId() {
        likeRepository.addLike(filmId, userId);

        List<Like> likes = likeRepository.findLikesByFilmId(filmId);

        assertThat(likes).hasSize(1);
    }

    // Вспомогательные методы для создания тестовых данных
    private User createTestUser(String email, String name, String login, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setLogin(login);
        user.setBirthday(birthday);
        return userRepository.create(user);
    }

    private Film createTestFilm(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        film.setMpaRating(new ru.yandex.practicum.filmorate.model.MpaRating(1L, "G"));
        return filmRepository.create(film);
    }
}