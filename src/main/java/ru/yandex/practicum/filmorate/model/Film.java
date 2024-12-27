package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

// 	•	@NotNull обеспечивает проверку на этапе обработки запросов.

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {

    private Long id;

    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть null")
    @NotBlank
    @Size(min = 1, max = 200, message = "Описание должно содержать от 1 до 200 символов")
    private String description;

    @NotNull(message = "Дата выхода не может быть null")
    private LocalDate releaseDate;

    @NotNull(message = "Длительность не может быть null")
    @Min(1)
    private Integer duration;

    private Set<Like> likes = new HashSet<>();

    @NotNull(message = "MPA рейтинг не может быть пустым")
    MpaRating mpaRating;

    Set<Genre> genres = new HashSet<>();
}
