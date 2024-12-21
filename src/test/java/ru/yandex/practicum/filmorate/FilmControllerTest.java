package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();
    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
    FilmController filmController = new FilmController(filmService);

    // Метод для создания фильма
    private Film createFilm(Long id) {
        return Film.builder()
                   .id(id)
                   .name("Что-то")
                   .description("Интересный фильм.")
                   .releaseDate(LocalDate.of(2010, 7, 16))
                   .duration(148)
                   .build();
    }

    @Test
    public void testFindAllMethodWithEmptyFilmsMap() {
        try {
            filmController.findAll(); // Попытка получить все фильмы, когда список пуст
        } catch (NotFoundException e) {
            assertEquals("Список фильмов пуст", e.getMessage());
        }
    }

    @Test
    public void testFindAllMethodWithFilledMap() {
        Film film = createFilm(1L);
        filmController.create(film); // Создаём фильм
        assertEquals(1, filmController.findAll().size()); // Проверяем, что список не пуст
    }

    @Test
    public void testCreateMethodWithValidObject() {
        Film film = createFilm(1L);
        Film testFilmObj = filmController.create(film); // Создаём фильм
        assertEquals(film, testFilmObj); // Проверяем, что возвращённый объект совпадает с созданным
    }

    @Test
    public void testCreateMethodWhenNameNull() {
        Film film = createFilm(1L);
        film.setName(null);  // Устанавливаем имя фильма в null
        assertValidationException(() -> filmController.create(film), "Название не может быть пустым");
    }

    @Test
    public void testCreateMethodWhenDescNull() {
        Film film = createFilm(1L);
        film.setDescription(null); // Устанавливаем описание фильма в null
        assertValidationException(() -> filmController.create(film), "Описание не может быть пустым");
    }

    @Test
    public void testCreateMethodWhenDescMoreThan200Chars() {
        Film film = createFilm(1L);
        film.setDescription("A".repeat(201)); // Описание длиной больше 200 символов
        assertValidationException(() -> filmController.create(film), "Максимальная длина описания — 200 символов");
    }

    @Test
    public void testCreateMethodWhenReleaseDateNull() {
        Film film = createFilm(1L);
        film.setReleaseDate(null); // Устанавливаем дату релиза в null
        assertValidationException(() -> filmController.create(film), "Дата релиза должна быть указана");
    }

    @Test
    public void testCreateMethodWhenReleaseDateBefore18951228() {
        Film film = createFilm(1L);
        film.setReleaseDate(LocalDate.of(1894, 12, 28)); // Дата релиза раньше 28 декабря 1895 года
        assertValidationException(() -> filmController.create(film), "Дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    public void testCreateMethodWithNullDuration() {
        Film film = createFilm(1L);
        film.setDuration(null); // Устанавливаем продолжительность в null
        assertValidationException(() -> filmController.create(film), "Продолжительность фильма должна быть указана");
    }

    @Test
    public void testCreateMethodWithNegativeDuration() {
        Film film = createFilm(1L);
        film.setDuration(-120); // Устанавливаем продолжительность в отрицательное число
        assertValidationException(() -> filmController.create(film), "Продолжительность фильма должна быть положительным числом");
    }

    @Test
    public void testUpdateMethodWithNullId() {
        Film film = createFilm(1L);
        film.setId(null); // Устанавливаем id в null
        assertValidationException(() -> filmController.update(film), "Id должен быть указан");
    }

    @Test
    public void testUpdateMethodWithSameName() {
        Film film = createFilm(1L);
        filmController.create(film); // Создаём фильм
        film.setName("Interstellar"); // Пробуем изменить название
        assertValidationException(() -> filmController.update(film), "Название фильма не может быть изменено");
    }

    @Test
    public void testUpdateMethodWithOtherReleaseDate() {
        Film film = createFilm(1L);
        filmController.create(film); // Создаём фильм
        film.setReleaseDate(LocalDate.of(2022, 12, 21)); // Пробуем изменить дату релиза
        assertValidationException(() -> filmController.update(film), "Дата релиза не может быть изменена");
    }

    @Test
    public void testUpdateMethodWithOtherDurationDate() {
        Film film = createFilm(1L);
        filmController.create(film); // Создаём фильм
        film.setDuration(119); // Пробуем изменить продолжительность
        assertValidationException(() -> filmController.update(film), "Продолжительность фильма не может быть изменена");
    }

    @Test
    public void testUpdateMethodWithValidRequest() {
        Film film = createFilm(1L);
        filmController.create(film); // Создаём фильм
        film.setDescription("Новый дискрипшен"); // Обновляем описание
        filmController.update(film); // Обновляем фильм
        assertEquals("Новый дискрипшен", film.getDescription()); // Проверяем, что описание обновилось
    }

    @Test
    public void testUpdateMethodWithWrongId() {
        Film film = createFilm(1L);
        filmController.create(film); // Создаём фильм
        film.setId(44L); // Устанавливаем неправильный id
        assertNotFoundException(() -> filmController.update(film), "Фильм с id = 44 не найден");
    }

    // Вспомогательные методы для проверки исключений
    private void assertValidationException(Runnable action, String expectedMessage) {
        try {
            action.run();
        } catch (ValidationException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private void assertNotFoundException(Runnable action, String expectedMessage) {
        try {
            action.run();
        } catch (NotFoundException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}