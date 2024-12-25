package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Обработчик всех необработанных исключений
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", "Произошла ошибка: " + e.getMessage());
        return error;
    }

    // Обработчик ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("errorMessage", "Произошла ошибка: " + ex.getMessage());
        return error;
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
}