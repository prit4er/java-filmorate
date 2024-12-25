package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        if (films.isEmpty()) {
            log.warn("Запрос на получение всех фильмов, но хранилище пусто");
            return Collections.emptyList();  // Возвращаем пустой список вместо null
        }
        return films.values();
    }

    public Film create(Film film) {
        validateFilm(film);

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

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        Optional.ofNullable(newFilm.getName()).ifPresent(oldFilm::setName);
        Optional.ofNullable(newFilm.getDescription()).ifPresent(oldFilm::setDescription);
        Optional.ofNullable(newFilm.getReleaseDate()).ifPresent(oldFilm::setReleaseDate);
        Optional.ofNullable(newFilm.getDuration()).ifPresent(oldFilm::setDuration);

        log.debug("Обновлен фильм с Id {}", newFilm.getId());
        return oldFilm;
    }

    private long getNextId() {
        return films.isEmpty() ? 1 : Collections.max(films.keySet()) + 1;
    }

    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза должна быть указана");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @Override
    public List<Film> findMostPopularFilms(int count) {
        List<Film> allFilmsList = new ArrayList<>(films.values());

        // Сортируем фильмы с использованием FilmLikesComparator
        allFilmsList.sort(new FilmLikesComparator().thenComparing(Film::getId));

        // Ограничиваем количество фильмов, если их больше, чем нужно
        return allFilmsList.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public void save(Film film) {
        films.put(film.getId(), film); // Добавление или обновление фильма
    }
}