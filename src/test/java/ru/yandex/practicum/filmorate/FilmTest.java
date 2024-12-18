package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidFilm() {
        Film film = Film.builder()
                        .name("Valid Film")
                        .description("A valid film description")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(120)
                        .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Film should be valid");
    }

    @Test
    void testInvalidName() {
        Film film = Film.builder()
                        .name("")
                        .description("Valid description")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(120)
                        .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Film with empty name should not be valid");
    }

    @Test
    void testInvalidDescriptionLength() {
        Film film = Film.builder()
                        .name("Valid Film")
                        .description("A".repeat(201)) // Exceeding the max length
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(120)
                        .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Film with description exceeding length should not be valid");
    }

    @Test
    void testNegativeDuration() {
        Film film = Film.builder()
                        .name("Valid Film")
                        .description("Valid description")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(-1) // Invalid negative duration
                        .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Film with negative duration should not be valid");
    }

    @Test
    void testNullReleaseDate() {
        Film film = Film.builder()
                        .name("Valid Film")
                        .description("Valid description")
                        .releaseDate(null) // Null release date
                        .duration(120)
                        .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Film with null release date should not be valid");
    }
}
