package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.dto.request.CreateUserRequest;
import ru.yandex.practicum.filmorate.dto.request.UpdateUserRequest;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- CreateUserRequest ---

    @Test
    void create_validUser_ShouldHaveNoViolations() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("validLogin");
        request.setName("Valid Name");
        request.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void create_email_WhenNullOrEmpty_ShouldFail(String email) {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(email);
        request.setLogin("login");
        request.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_email_WhenMissingAt_ShouldFail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("userexample.com");
        request.setLogin("login");
        request.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void create_login_WhenNullOrEmpty_ShouldFail(String login) {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin(login);
        request.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_login_WhenContainsSpace_ShouldFail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("user login");
        request.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Логин не должен содержать пробелы"));
    }

    @Test
    void create_login_WhenValid_ShouldSucceed() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("valid_login123");
        request.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_birthday_WhenNull_ShouldSucceed() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("login");
        request.setBirthday(null);

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_birthday_WhenFutureDate_ShouldFail() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("login");
        request.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void create_birthday_WhenPast_ShouldSucceed() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("login");
        request.setBirthday(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void create_name_WhenNullOrBlank_GetterReturnsLogin(String name) {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("myLogin");
        request.setName(name);

        assertEquals("myLogin", request.getName());
    }

    @Test
    void create_name_WhenProvided_GetterReturnsIt() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setLogin("myLogin");
        request.setName("John Doe");

        assertEquals("John Doe", request.getName());
    }

    // --- UpdateUserRequest ---

    @Test
    void update_validUser_ShouldHaveNoViolations() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(1L);
        request.setEmail("user@example.com");
        request.setLogin("validLogin");
        request.setName("Valid Name");
        request.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void update_id_WhenNull_ShouldFail() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(null);
        request.setEmail("user@example.com");
        request.setLogin("login");
        request.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("id", violations.iterator().next().getPropertyPath().toString());
    }
}
