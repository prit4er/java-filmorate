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
public class Friendship {

    private Long id;

    @NotNull(message = "sender не может быть null")
    private Long sender;

    @NotNull(message = "receiver не может быть null")
    private Long receiver;
}