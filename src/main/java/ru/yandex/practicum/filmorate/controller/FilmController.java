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
        @PutMapping("/{id}/like/{userId}")
        public void addLike(@PathVariable long id, @PathVariable long userId) {
            log.info("Получен запрос на добавление лайка фильму с id {} от пользователя с id {}.", id, userId);
            filmService.addLike(id, userId);
        }

        @DeleteMapping("/{id}/like/{userId}")
        public void deleteLike(@PathVariable long id, @PathVariable long userId) {
            log.info("Получен запрос на удаление лайка у фильма с id {} от пользователя с id {}.", id, userId);
            filmService.removeLike(id, userId);
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