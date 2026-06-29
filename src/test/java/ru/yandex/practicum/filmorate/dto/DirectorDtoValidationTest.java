package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.request.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DirectorDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreateDirectorRequest validCreateRequest() {
        CreateDirectorRequest request = new CreateDirectorRequest();
        request.setName("Name");
        return request;
    }

    private UpdateDirectorRequest validUpdateRequest() {
        UpdateDirectorRequest request = new UpdateDirectorRequest();
        request.setId(1L);
        request.setName("Name");
        return request;
    }

    @Test
    void create_name_WhenNull_ShouldFail() {
        CreateDirectorRequest request = validCreateRequest();
        request.setName(null);

        Set<ConstraintViolation<CreateDirectorRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void create_name_WhenBlank_ShouldFail() {
        CreateDirectorRequest request = validCreateRequest();
        request.setName("   ");

        Set<ConstraintViolation<CreateDirectorRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void update_id_WhenNull_ShouldFail() {
        UpdateDirectorRequest request = validUpdateRequest();
        request.setId(null);

        Set<ConstraintViolation<UpdateDirectorRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("id", violations.iterator().next().getPropertyPath().toString());
    }

}
