package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import ru.yandex.practicum.filmorate.dto.request.CreateReviewRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateReviewRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReviewDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateReviewRequest validCreateRequest() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setContent("10/10");
        request.setPositive(true);
        request.setUserId(1L);
        request.setFilmId(1L);
        return request;
    }

    private UpdateReviewRequest validUpdateRequest() {
        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setReviewId(1L);
        request.setContent("Updated review");
        request.setPositive(false);
        request.setUserId(1L);
        request.setFilmId(1L);
        return request;
    }

    @ParameterizedTest
    @NullAndEmptySource
    void create_content_WhenNullOrEmpty_ShouldFail(String content) {
        CreateReviewRequest request = validCreateRequest();
        request.setContent(content);
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("content", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_isPositive_WhenNull_ShouldFail() {
        CreateReviewRequest request = validCreateRequest();
        request.setPositive(null);
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("isPositive")));
    }

    @Test
    void create_userId_WhenNull_ShouldFail() {
        CreateReviewRequest request = validCreateRequest();
        request.setUserId(null);
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("userId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_filmId_WhenNull_ShouldFail() {
        CreateReviewRequest request = validCreateRequest();
        request.setFilmId(null);
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("filmId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_valid_ShouldSucceed() {
        CreateReviewRequest request = validCreateRequest();
        Set<ConstraintViolation<CreateReviewRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void update_reviewId_WhenNull_ShouldFail() {
        UpdateReviewRequest request = validUpdateRequest();
        request.setReviewId(0);
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("reviewId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_reviewId_WhenNegative_ShouldFail() {
        UpdateReviewRequest request = validUpdateRequest();
        request.setReviewId(-1);
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("reviewId", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void update_content_WhenNullOrEmpty_ShouldFail(String content) {
        UpdateReviewRequest request = validUpdateRequest();
        request.setContent(content);
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("content", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_isPositive_WhenNull_ShouldFail() {
        UpdateReviewRequest request = validUpdateRequest();
        request.setPositive(null);
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void update_userId_WhenNull_ShouldFail() {
        UpdateReviewRequest request = validUpdateRequest();
        request.setUserId(null);
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("userId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_filmId_WhenNull_ShouldFail() {
        UpdateReviewRequest request = validUpdateRequest();
        request.setFilmId(null);
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("filmId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void update_valid_ShouldSucceed() {
        UpdateReviewRequest request = validUpdateRequest();
        Set<ConstraintViolation<UpdateReviewRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}
