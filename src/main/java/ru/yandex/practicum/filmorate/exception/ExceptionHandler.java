package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    // Обработчик всех необработанных исключений
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Необработанная ошибка: ", e);
        return Map.of("errorMessage", "Произошла ошибка: " + e.getMessage());
    }

    // Обработчик ValidationException
    @org.springframework.web.bind.annotation.ExceptionHandler
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return Map.of("errorMessage", e.getMessage());
    }

    // Обработчик NotFoundException
    @org.springframework.web.bind.annotation.ExceptionHandler
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.warn("Не найден объект: {}", e.getMessage());
        return Map.of("errorMessage", e.getMessage());
    }
}