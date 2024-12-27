package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmGenre {

    Long id;

    @NotNull(message = "filmId не может быть null")
    Long filmId;

    @NotNull(message = "genreId не может быть null")
    Long genreId;
}