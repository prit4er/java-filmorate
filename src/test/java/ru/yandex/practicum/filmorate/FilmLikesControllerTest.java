package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmLikesControllerTest {

    private InMemoryFilmStorage inMemoryFilmStorage;
    private InMemoryUserStorage inMemoryUserStorage;
    private FilmService filmService;
    private UserService userService;
    private FilmController filmController;

    private Film film1;
    private Film film2;
    private User user0;
    private User userPostman;

    @BeforeEach
    public void setUp() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
        userService = new UserService(inMemoryUserStorage);
        filmController = new FilmController(filmService);

        user0 = User.builder()
                    .id(1L)
                    .email("john.doe@mail.com")
                    .login("johndoe")
                    .name("John Doe")
                    .birthday(LocalDate.of(1985, 5, 15))
                    .build();

        userPostman = User.builder()
                          .login("nick123")
                          .name("Nick Johnson")
                          .email("nick.johnson@mail.com")
                          .birthday(LocalDate.of(1990, 3, 25))
                          .build();

        film1 = Film.builder()
                    .id(1L)
                    .name("Film 1")
                    .description("First film")
                    .releaseDate(LocalDate.of(2020, 5, 5))
                    .duration(120)
                    .build();

        film2 = Film.builder()
                    .id(2L)
                    .name("Film 2")
                    .description("Second film")
                    .releaseDate(LocalDate.of(2021, 6, 10))
                    .duration(150)
                    .build();
    }

    @Test
    public void testAddLike() {
        filmController.create(film1);
        userService.create(user0);
        userService.create(userPostman);

        filmController.addLike(film1.getId(), user0.getId());
        filmController.addLike(film1.getId(), userPostman.getId());

        Film updatedFilm = filmController.getFilmById(film1.getId());
        assertEquals(2, updatedFilm.getLikes().size(), "Фильм должен иметь 2 лайка");
    }

    @Test
    public void testRemoveLike() {
        filmController.create(film1);
        userService.create(user0);
        userService.create(userPostman);

        filmController.addLike(film1.getId(), user0.getId());
        filmController.addLike(film1.getId(), userPostman.getId());

        filmController.removeLike(film1.getId(), userPostman.getId());
        Film updatedFilm = filmController.getFilmById(film1.getId());

        assertEquals(1, updatedFilm.getLikes().size(), "Фильм должен иметь 1 лайк после удаления");
    }

    @Test
    public void testGetPopularFilms() {
        filmController.create(film1);
        filmController.create(film2);
        userService.create(user0);
        userService.create(userPostman);

        filmController.addLike(film1.getId(), user0.getId());
        filmController.addLike(film1.getId(), userPostman.getId());
        filmController.addLike(film2.getId(), user0.getId());

        List<Film> popularFilms = filmController.getMostPopularFilms(1);
        assertEquals(1, popularFilms.size(), "Должен быть только один самый популярный фильм");
        assertEquals(film1.getId(), popularFilms.get(0).getId(), "Самый популярный фильм должен быть первым");
    }
}