package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    Film film0 = Film.builder()
                     .id(1L)
                     .name("Что то")
                     .description("Ну интересный фильм.")
                     .releaseDate(LocalDate.of(2010, 7, 16))
                     .duration(148)
                     .build();

    @Test
    public void testFindAllMethodWithEmptyFilmsMap() {
        try {
            filmController.findAll();
        } catch (NotFoundException e) {
            assertEquals("Список фильмов пуст", e.getMessage());
        }
    }

    @Test
    public void testFindAllMethodWithFilledMap() {
        filmController.create(film0);
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void testCreateMethodWithValidObject() {
        Film testFilmObj = filmController.create(film0);
        assertEquals(film0, testFilmObj);
    }

    @Test
    public void testCreateMethodWhenNameNull() {
        film0.setName(null);
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Название не может быть пустым", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWhenDescNull() {
        film0.setDescription(null);
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Описание не может быть пустым", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWhenDescMoreThan200Chars() {
        String desc = "A".repeat(201); // 201 character description
        film0.setDescription(desc);
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Максимальная длина описания — 200 символов", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWhenReleaseDateNull() {
        film0.setReleaseDate(null);
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Дата релиза должна быть указана", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWhenReleaseDateBefore18951228() {
        film0.setReleaseDate(LocalDate.of(1894, 12, 28));
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithNullDuration() {
        film0.setDuration(null);
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Продолжительность фильма должна быть указана", e.getMessage());
        }
    }

    @Test
    public void testCreateMethodWithNegativeDuration() {
        film0.setDuration(-120);
        try {
            filmController.create(film0);
        } catch (ValidationException e) {
            assertEquals("Продолжительность фильма должна быть положительным числом", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithNullId() {
        filmController.create(film0);
        film0.setId(null);
        try {
            filmController.update(film0);
        } catch (NotFoundException e) {
            assertEquals("Фильм с id = null не найден", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithSameName() {
        filmController.create(film0);
        Film film1 = film0;
        film1.setName("Interstellar");
        try {
            filmController.update(film1);
        } catch (ValidationException e) {
            assertEquals("Название фильма не может быть изменено", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithOtherReleaseDate() {
        filmController.create(film0);
        Film film1 = film0;
        film1.setReleaseDate(LocalDate.of(2022, 12, 21));
        try {
            filmController.update(film1);
        } catch (ValidationException e) {
            assertEquals("Дата релиза не может быть изменена", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithOtherDurationDate() {
        filmController.create(film0);
        Film film1 = film0;
        film1.setDuration(119);
        try {
            filmController.update(film1);
        } catch (ValidationException e) {
            assertEquals("Продолжительность фильма не может быть изменена", e.getMessage());
        }
    }

    @Test
    public void testUpdateMethodWithValidRequest() {
        filmController.create(film0);
        Film film1 = film0;
        film1.setDescription("Новый дискрипшен");
        filmController.update(film1);
        assertEquals("Новый дискрипшен", film1.getDescription());
    }

    @Test
    public void testUpdateMethodWithWrongId() {
        filmController.create(film0);
        Film film1 = film0;
        film1.setId(44L);
        try {
            filmController.update(film1);
        } catch (NotFoundException e) {
            assertEquals("Фильм с id = 44 не найден", e.getMessage());
        }
    }
}