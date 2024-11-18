package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// 	•	@NotNull обеспечивает проверку на этапе обработки запросов.

@Data
@Builder(toBuilder = true)
public class Film {

    @Builder.Default
    private Long id = Long.valueOf(0);

    @Builder.Default
    @NotNull(message = "Имя не может быть null")
    @NotBlank(message = "Имя не может быть пустым")
    private String name = "Default name";

    @Builder.Default
    @NotNull(message = "Описание не может быть null")
    @NotBlank
    @Size(min = 1, max = 200, message = "Описание должно содержать от 1 до 200 символов")
    private String description = "Default description";

    @Builder.Default
    @NotNull(message = "Дата выхода не может быть null")
    private LocalDate releaseDate = LocalDate.of(2000, 12, 28);

    @Builder.Default
    @NotNull(message = "Длительность не может быть null")
    @Min(1)
    private Integer duration = Integer.valueOf(110);
}
