package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        if (films.isEmpty()) {
            log.warn("Запрос на получение всех фильмов, но хранилище пусто");
            return Collections.emptyList();  // Возвращаем пустой список вместо null
        }
        return films.values();
    }

    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Ошибка при добавлении фильма: пустое имя");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.error("Ошибка при добавлении фильма: пустое описание");
            throw new ValidationException("Описание не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.error("Ошибка при добавлении фильма: описание слишком длинное");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.error("Ошибка при добавлении фильма: нет даты релиза");
            throw new ValidationException("Дата релиза должна быть указана");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при добавлении фильма: дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null) {
            log.error("Ошибка при добавлении фильма: нет продолжительности");
            throw new ValidationException("Продолжительность фильма должна быть указана");
        } else if (film.getDuration() < 0) {
            log.error("Ошибка при добавлении фильма: отрицательная продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        if (film.getId() == null) {
            film.setId(getNextId());
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        films.put(film.getId(), film);
        log.debug("Добавлен фильм с Id {}", film.getId());
        return film;
    }

    private long getNextId() {
        return films.keySet()
                    .stream()
                    .mapToLong(id -> id)
                    .max()
                    .orElse(0) + 1;  // Просто инкрементируем максимальный id
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.error("Ошибка при обновлении фильма: Id не указан");
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.error("Ошибка при обновлении фильма: фильм с id = {} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        // Обновление полей фильма
        if (newFilm.getName() != null && !newFilm.getName().isEmpty()) {
            oldFilm.setName(newFilm.getName());
            log.trace("Изменено название фильма с Id {}", newFilm.getId());
        }
        if (newFilm.getDescription() != null && !newFilm.getDescription().isEmpty()) {
            oldFilm.setDescription(newFilm.getDescription());
            log.trace("Изменено описание фильма с Id {}", newFilm.getId());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.trace("Изменена дата релиза фильма с Id {}", newFilm.getId());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
            log.trace("Изменена продолжительность фильма с Id {}", newFilm.getId());
        }

        log.debug("Обновлен фильм с Id {}", newFilm.getId());
        return oldFilm;
    }

    public Optional<Film> getFilmById(Long id) {
        if (films.isEmpty()) {
            log.error("Ошибка при получении фильма: хранилище пусто");
            return Optional.empty();
        }

        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        }

        log.error("Фильм с id = {} не найден", id);
        return Optional.empty();
    }
}