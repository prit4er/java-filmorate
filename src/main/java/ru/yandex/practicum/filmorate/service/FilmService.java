package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmRepository;
import ru.yandex.practicum.filmorate.storage.GenreRepository;
import ru.yandex.practicum.filmorate.storage.LikeRepository;
import ru.yandex.practicum.filmorate.storage.MpaRatingRepository;
import ru.yandex.practicum.filmorate.storage.UserRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final MpaRatingRepository mpaRatingRepository;

    public List<FilmDto> findAll() {
        List<Film> films = filmRepository.findAll();
        Map<Long, Set<Genre>> genresByFilm = genreRepository.findGenresForFilms(
                films.stream().map(Film::getId).toList()
        );
        films.forEach(film -> film.setGenres(genresByFilm.getOrDefault(film.getId(), new LinkedHashSet<>())));
        return films.stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
    }

    public Optional<FilmDto> findById(Long id) {
        Optional<Film> filmOptional = filmRepository.findById(id);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();
            Map<Long, Set<Genre>> genresByFilm = genreRepository.findGenresForFilms(List.of(film.getId()));
            film.setGenres(genresByFilm.getOrDefault(film.getId(), new LinkedHashSet<>()));
            return Optional.of(FilmMapper.mapToFilmDto(film));
        }
        return Optional.empty();
    }

    public FilmDto create(Film film) {
        checkReleaseDate(film);
        checkGenres(film);
        checkRating(film);
        Film createdFilm = filmRepository.create(film);
        Map<Long, Set<Genre>> genresByFilm = genreRepository.findGenresForFilms(List.of(createdFilm.getId()));
        createdFilm.setGenres(genresByFilm.getOrDefault(createdFilm.getId(), new LinkedHashSet<>()));
        return FilmMapper.mapToFilmDto(createdFilm);
    }

    public FilmDto update(Film film) {
        filmRepository.findById(film.getId())
                      .orElseThrow(() -> new NotFoundException(
                              String.format("Фильм с таким id: %d - отсутствует", film.getId())
                      ));
        checkGenres(film);
        checkReleaseDate(film);
        checkRating(film);
        Film updatedFilm = filmRepository.update(film);
        Map<Long, Set<Genre>> genresByFilm = genreRepository.findGenresForFilms(List.of(updatedFilm.getId()));
        updatedFilm.setGenres(genresByFilm.getOrDefault(updatedFilm.getId(), new LinkedHashSet<>()));
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void addLike(Long filmId, Long userId) {
        // Проверка существования фильма
        Film film = filmRepository.findById(filmId)
                                  .orElseThrow(() -> new NotFoundException("Film with id " + filmId + " not found"));

        // Проверка существования пользователя
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        // Сохраняем лайк в репозитории лайков (для учета)
        likeRepository.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeRepository.deleteLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> films = filmRepository.findAll();
        Map<Long, Set<Genre>> genresByFilm = genreRepository.findGenresForFilms(
                films.stream().map(Film::getId).toList()
        );
        films.forEach(film -> film.setGenres(genresByFilm.getOrDefault(film.getId(), new LinkedHashSet<>())));
        films.forEach(film -> {
            List<Like> likes = likeRepository.findLikesByFilmId(film.getId());
            film.setLikes(new HashSet<>(likes));
        });
        return films.stream()
                    .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                    .limit(count)
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода не может быть раньше " +
                                                  "28.12.1895 - даты выхода первого в истории фильма");
        }
    }

    private void checkGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            // Проверка, существует ли жанр в базе
            genreRepository.findById(genre.getId())
                           .orElseThrow(() -> new ValidationException(
                                   String.format("Жанр с таким id: %d - отсутствует", genre.getId())
                           ));
        }
    }

    private void checkRating(Film film) {
        MpaRating mpaRating = film.getMpaRating();  // Получаем рейтинг
        if (mpaRating == null || !mpaRatingRepository.findById(mpaRating.getId()).isPresent()) {
            throw new ValidationException(
                    String.format("Рейтинга с таким id: %d - нет", mpaRating.getId())
            );
        }
    }
}
