package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final String GENRES_PATH = "/genres";
    private final String GENRES_ID_PATH = "/genres/{id}";

    private final GenreService genreService;

    @GetMapping(GENRES_PATH)
    public List<GenreDto> findAll() {
        return genreService.findAll();
    }

    @GetMapping(GENRES_ID_PATH)
    public GenreDto findFilm(@PathVariable Long id) {
        return genreService.findById(id);
    }
}