package ru.yandex.practicum.filmorate.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter @Data
public class ErrorResponse {
    private String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}