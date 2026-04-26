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

class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUser_ShouldHaveNoViolations() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void email_WhenMissingAt_ShouldFail() {
        User user = new User();
        user.setEmail("userexample.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void login_WhenNull_ShouldFail() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin(null);
        user.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("login", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void login_WhenContainsSpace_ShouldFail() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user login"); // содержит пробел
        user.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertTrue(violations.iterator().next().getMessage().contains("Логин не должен содержать пробелы"));
    }

    @Test
    void login_WhenValid_ShouldSucceed() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("valid_login123");
        user.setBirthday(LocalDate.now().minusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void birthday_WhenFutureDate_ShouldFail() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // будущая дата

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void birthday_WhenPastOrPresent_ShouldSucceed() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void name_WhenNull_GetterReturnsLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("myLogin");
        user.setName(null); // явно null

        assertEquals("myLogin", user.getName());
    }

    @Test
    void name_WhenEmpty_GetterReturnsLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("myLogin");
        user.setName("");

        assertEquals("myLogin", user.getName());
    }

    @Test
    void name_WhenBlank_GetterReturnsLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("myLogin");
        user.setName("   ");

        assertEquals("myLogin", user.getName());
    }

    @Test
    void name_WhenProvided_GetterReturnsIt() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("myLogin");
        user.setName("John Doe");

        assertEquals("John Doe", user.getName());
    }
}
