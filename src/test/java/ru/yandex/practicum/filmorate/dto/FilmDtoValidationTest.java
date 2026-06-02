package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.request.CreateFilmRequest;
import ru.yandex.practicum.filmorate.dto.request.MpaDto;
import ru.yandex.practicum.filmorate.dto.request.UpdateFilmRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateFilmRequest validCreateRequest() {
        CreateFilmRequest request = new CreateFilmRequest();
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(new MpaDto());
        request.setGenres(List.of());
        return request;
    }

    private UpdateFilmRequest validUpdateRequest() {
        UpdateFilmRequest request = new UpdateFilmRequest();
        request.setId(1L);
        request.setName("Name");
        request.setReleaseDate(LocalDate.now());
        request.setDuration(100L);
        request.setMpa(new MpaDto());
        request.setGenres(List.of());
        return request;
    }

    @Test
    void create_name_WhenNull_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setName(null);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_name_WhenBlank_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setName("   ");

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_description_WhenExactly200Chars_ShouldSucceed() {
        CreateFilmRequest request = validCreateRequest();
        request.setDescription("A".repeat(200));

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_description_When201Chars_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setDescription("A".repeat(201));

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("description", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_releaseDate_WhenNull_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setReleaseDate(null);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("releaseDate", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_releaseDate_WhenBeforeBoundary_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        String message = violations.iterator().next().getMessage();
        assertTrue(message.contains("Дата релиза должна быть не раньше 28 декабря 1895 года"));
    }

    @Test
    void create_releaseDate_ExactlyBoundary_ShouldSucceed() {
        CreateFilmRequest request = validCreateRequest();
        request.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_releaseDate_AfterBoundary_ShouldSucceed() {
        CreateFilmRequest request = validCreateRequest();
        request.setReleaseDate(LocalDate.of(2020, 1, 1));

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_duration_WhenZero_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setDuration(0L);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_duration_WhenNegative_ShouldFail() {
        CreateFilmRequest request = validCreateRequest();
        request.setDuration(-10L);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_duration_WhenPositive_ShouldSucceed() {
        CreateFilmRequest request = validCreateRequest();
        request.setDuration(10L);

        Set<ConstraintViolation<CreateFilmRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void update_id_WhenNull_ShouldFail() {
        UpdateFilmRequest request = validUpdateRequest();
        request.setId(null);

        Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("id", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_releaseDate_WhenNull_ShouldFail() {
        UpdateFilmRequest request = validUpdateRequest();
        request.setReleaseDate(null);

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
