package ru.yandex.practicum.filmorate.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class ErrorResponse {

    private final String error;
    private final String message;

}