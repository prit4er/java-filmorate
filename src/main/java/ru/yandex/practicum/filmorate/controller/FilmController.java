package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    // Константы маршрутов
    private final String filmsIdPath = "/{id}";
    private final String likePath = "/{id}/like/{user-id}";
    private final String popularPath = "/popular";

    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping(filmsIdPath)
    public Optional<FilmDto> findFilm(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody FilmDto filmDto) {
        return filmService.create(FilmMapper.mapToFilm(filmDto));
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto filmDto) {
        return filmService.update(FilmMapper.mapToFilm(filmDto));
    }

    @PutMapping(likePath)
    public void addLike(@PathVariable Long id, @PathVariable("user-id") Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(likePath)
    public void deleteLike(@PathVariable Long id, @PathVariable("user-id") Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping(popularPath)
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}