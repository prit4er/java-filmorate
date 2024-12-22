package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

// 	•	@NotNull обеспечивает проверку на этапе обработки запросов.

@Data
@Builder(toBuilder = true)
public class Film implements Comparable<Film> {

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

    private Set<Long> likes;

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                '}';
    }

    public int compareTo(Film obj) {
        return obj.getLikes().size() - this.getLikes().size();
    }
}
