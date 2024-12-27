package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class FilmGenre {

    private Long id;

    @NotNull(message = "filmId не может быть null")
    private Long filmId;

    @NotNull(message = "genreId не может быть null")
    private Long genreId;
}