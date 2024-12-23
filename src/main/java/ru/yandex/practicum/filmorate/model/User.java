package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;

    // Электронная почта не может быть пустой и должна содержать символ @
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в формате 'example@domain.com'")
    @NotNull
    private String email;

    // Логин не может быть пустым и не может содержать пробелы
    @NotBlank(message = "Логин не может быть пустым")
    @NotNull(message = "Логин не может быть null")
    @Pattern(regexp = "^[^\\s]*$", message = "Логин не может содержать пробелы")
    private String login;

    // Имя для отображения может быть пустым — в таком случае будет использован логин
    private String name;

    // Дата рождения не может быть в будущем
    @Past(message = "Дата рождения не может быть в будущем")
    @NotNull
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();

    public void removeFromFriends(Long id) {
        friends.remove(id);
    }

    public boolean isFriend(Long id) {
        return friends.contains(id);
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", friends=" + friends +
                '}';
    }
}