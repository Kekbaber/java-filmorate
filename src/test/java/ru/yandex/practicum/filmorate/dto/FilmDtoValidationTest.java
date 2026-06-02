package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- CreateFilmRequest ---

    @Test
    void create_validFilm_ShouldHaveNoViolations() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Valid Name");
        request.setDescription("Valid description");
        request.setReleaseDate(LocalDate.of(2023, 1, 1));
        request.setDuration(120L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_name_WhenNull_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName(null);
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_name_WhenBlank_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("   ");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_description_WhenExactly200Chars_ShouldSucceed() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setDescription("A".repeat(200));
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_description_When201Chars_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setDescription("A".repeat(201));
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_releaseDate_WhenNull_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(null);
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("releaseDate", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_releaseDate_WhenBeforeBoundary_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.of(1895, 12, 27));
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        String message = violations.iterator().next().getMessage();
        assertTrue(message.contains("Дата релиза должна быть не раньше 28 декабря 1895 года"));
    }

    @Test
    void create_releaseDate_ExactlyBoundary_ShouldSucceed() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.of(1895, 12, 28));
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_releaseDate_AfterBoundary_ShouldSucceed() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.of(2020, 1, 1));
        request.setDuration(100L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_duration_WhenZero_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(0L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_duration_WhenNegative_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(-10L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_duration_WhenNull_ShouldSucceed() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(null);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_duration_WhenPositive_ShouldSucceed() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(10L);
        request.setMpa(new Mpa());

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_mpa_WhenNull_ShouldFail() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(null);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("mpa", violations.iterator().next().getPropertyPath().toString());
    }

    // --- UpdateFilmRequest ---

    @Test
    void update_validFilm_ShouldHaveNoViolations() {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(1L);
        request.setName("Valid Name");
        request.setDescription("Valid description");
        request.setReleaseDate(LocalDate.of(2023, 1, 1));
        request.setDuration(120L);

        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void update_id_WhenNull_ShouldFail() {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(null);
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);

        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("id", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_releaseDate_WhenNull_ShouldFail() {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(1L);
        request.setName("Name");
        request.setReleaseDate(null);
        request.setDuration(100L);

        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("releaseDate", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_mpa_WhenNull_ShouldSucceed() {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(1L);
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(null);

        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
