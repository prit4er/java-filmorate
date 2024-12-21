package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Data
public class FilmService {

    private final FilmStorage filmStorage;  // Хранилище фильмов
    private final UserStorage inMemoryUserStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage inMemoryUserStorage) {
        this.filmStorage = filmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    // Метод для получения всех фильмов
    public List<Film> findAll() {
        return new ArrayList<>(filmStorage.findAll());
    }

    // Метод для создания нового фильма
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    // Метод для обновления фильма
    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    // Метод для получения фильма по ID
    public Optional<Film> getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    // Методы для лайков (вы уже добавили их в вашем классе)
    public void giveLike(Long userId, Long filmId) {
        Optional<User> user = inMemoryUserStorage.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Попытка поставить лайк фильму с несуществующим пользователем с id {}", userId);
            throw new NotFoundException("Юзер с id " + userId + " отсутствует");
        }

        Optional<Film> film = filmStorage.getFilmById(filmId);
        if (film.isPresent()) {
            film.get().addLike(userId);
            log.trace("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
        } else {
            log.error("Попытка поставить лайк несуществующему фильму с id {}", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " отсутствует.");
        }
    }

    // Удаление лайка
    public void removeLike(Long userId, Long filmId) {
        Optional<User> user = inMemoryUserStorage.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Попытка удалить лайк фильму с несуществующим пользователем с id {}", userId);
            throw new NotFoundException("Юзер с id " + userId + " отсутствует");
        }

        Optional<Film> film = filmStorage.getFilmById(filmId);
        if (film.isPresent()) {
            film.get().removeLike(userId);
            log.trace("Пользователь с id {} удалил лайк у фильма с id {}", userId, filmId);
        } else {
            log.error("Попытка удалить лайк у несуществующего фильма с id {}", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " отсутствует.");
        }
    }

    // Получить самые популярные фильмы
    public List<Film> getMostPopularFilms(Integer count) {
        List<Film> allFilmsList = new ArrayList<>(filmStorage.findAll());

        if (allFilmsList.size() <= count) {
            return allFilmsList;
        }

        Collections.sort(allFilmsList);
        return allFilmsList.subList(0, count);
    }
}