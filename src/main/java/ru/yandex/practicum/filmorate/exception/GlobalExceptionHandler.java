package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValid(final ValidationException e) {
        log.error("Ошибка валидации: ", e);
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    // Обработчик NotFoundException
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.warn("Не найдено: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", ex.getMessage());
        return error;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicated(final DuplicatedDataException e) {
        log.error("Дублирование данных: ", e);
        return new ErrorResponse("Дублирование данных", e.getMessage());
    }
}