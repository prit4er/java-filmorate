package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        log.info("Запрос на получение списка всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создание нового фильма: {}", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма с Id: {}", newFilm.getId());
        return filmService.update(newFilm);
    }

    @PutMapping("/{filmId}/likes/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        try {
            log.info("Пользователь с id = {} ставит лайк фильму с id = {}", userId, filmId);
            filmService.addLike(filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении лайка к фильму с id = {} пользователем с id = {}", filmId, userId, e);
            throw e;
        }
    }

    @DeleteMapping("/{filmId}/likes/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        try {
            log.info("Пользователь с id = {} удаляет лайк с фильма с id = {}", userId, filmId);
            filmService.removeLike(filmId, userId);
        } catch (Exception e) {
            log.error("Ошибка при удалении лайка с фильма с id = {} пользователем с id = {}", filmId, userId, e);
            throw e;
        }
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение популярных фильмов, ограничение по количеству: {}", count);
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        log.info("Запрос на получение фильма с id = {}", filmId);
        return filmService.getFilmById(filmId);
    }
}