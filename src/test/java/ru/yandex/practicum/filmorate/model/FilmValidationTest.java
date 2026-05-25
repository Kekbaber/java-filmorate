package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validFilm_ShouldHaveNoViolations() {
        Film film = new Film();
        film.setName("Valid Name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2023, 1, 1));
        film.setDuration(120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Valid film should not have violations");
    }

    @Test
    void name_WhenNull_ShouldFail() {
        Film film = new Film();
        film.setName(null);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertTrue(violation.getMessage().contains("не может быть пустым"));
    }

    @Test
    void name_WhenBlank_ShouldFail() {
        Film film = new Film();
        film.setName("   ");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void description_WhenExactly200Chars_ShouldSucceed() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(200));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void description_When201Chars_ShouldFail() {
        Film film = new Film();
        film.setName("Name");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void releaseDate_WhenBeforeBoundary_ShouldFail() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        String message = violations.iterator().next().getMessage();
        assertTrue(message.contains("Дата релиза должна быть не раньше 28 декабря 1895 года"));
    }

    @Test
    void releaseDate_ExactlyBoundary_ShouldSucceed() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(1895, 12, 28)); // точная граница
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void releaseDate_AfterBoundary_ShouldSucceed() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(100L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void duration_WhenZero_ShouldFail() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(0L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void duration_WhenNegative_ShouldFail() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-10L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void duration_WhenPositive_ShouldSucceed() {
        Film film = new Film();
        film.setName("Name");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}
