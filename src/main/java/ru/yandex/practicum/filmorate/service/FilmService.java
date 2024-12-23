package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Data
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;  // Хранилище фильмов
    private final UserStorage inMemoryUserStorage;

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
    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                          .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден."));
    }

    // Новый метод для получения пользователя по ID с обработкой отсутствия
    private User getUser(Long userId) {
        return inMemoryUserStorage.getUserById(userId)
                                  .orElseThrow(() -> new NotFoundException("Юзер с id = " + userId + " не найден."));
    }

    // Методы для лайков
    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = getUser(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " уже поставил лайк.");
        }

        film.getLikes().add(userId);
        filmStorage.save(film);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = getUser(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Пользователь с id = " + userId + " не ставил лайк.");
        }

        film.getLikes().remove(userId);
        filmStorage.save(film);
    }

    // Получить самые популярные фильмы
    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.findMostPopularFilms(count);  // Используем метод репозитория
    }
}