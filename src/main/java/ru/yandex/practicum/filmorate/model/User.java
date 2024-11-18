package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {

    @Builder.Default
    private Long id = Long.valueOf(0);

    // Электронная почта не может быть пустой и должна содержать символ @
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в формате 'example@domain.com'")
    @NotNull
    private String email = "default@example.com";

    // Логин не может быть пустым и не может содержать пробелы
    @NotBlank(message = "Логин не может быть пустым")
    @NotNull(message = "Логин не может быть null")
    @Pattern(regexp = "^[^\\s]*$", message = "Логин не может содержать пробелы")
    private String login = "defaultLogin";

    // Имя для отображения может быть пустым — в таком случае будет использован логин
    @NotNull(message = "Имя не может быть null")
    private String name = "Default name";

    // Дата рождения не может быть в будущем
    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull
    private LocalDate birthday = LocalDate.of(2000, 12, 01);

    // Кастомная логика для установки имени по умолчанию как логин, если имя пустое
    public String getName() {
        if (name == null || name.isBlank()) {
            return login;  // Используем логин, если имя пустое
        }
        return name;
    }
}