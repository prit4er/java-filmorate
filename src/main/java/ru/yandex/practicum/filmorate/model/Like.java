package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Like {

    private Long id;

    @NotNull(message = "filmId не может быть null")
    private Long filmId;

    @NotNull(message = "userId не может быть null")
    private Long userId;
}