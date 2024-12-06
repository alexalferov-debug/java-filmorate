package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldInvalidUserWhenEmailIsNull() {
        User user = new User();
        user.setEmail(null); // Не устанавливаем email
        user.setLogin("testUser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Электронная почта не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidUserWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalidEmail"); // Неправильный email
        user.setLogin("testUser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Некорректный формат электронной почты", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidUserWhenLoginIsNull() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin(null); // Не устанавливаем логин
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size());
        assertEquals("Логин не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidUserWhenLoginContainsSpace() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("test user"); // Неправильный логин
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidUserWhenBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testUser");
        user.setBirthday(LocalDate.now().plusDays(1)); // Будущая дата

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldNotHaveViolationsForValidUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testUser");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size()); // Должно быть без нарушений
    }
}
