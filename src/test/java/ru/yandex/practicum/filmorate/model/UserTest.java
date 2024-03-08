package ru.yandex.practicum.filmorate.model;


import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    private User createUserWithDefaults() {
        return User.builder()
                .email("normal.email@example.com")
                .login("normal_login")
                .birthday(LocalDate.now().minusYears(20))
                .build();
    }

    @Test
    public void testInvalidEmail() {
        User user = createUserWithDefaults();
        user.setEmail("invalid_email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        ConstraintViolation<User> userConstraintViolation = violations.stream().findFirst().orElse(null);

        System.out.println(userConstraintViolation.getMessage());

        assertEquals(1, violations.size());
        assertEquals("Invalid email format", userConstraintViolation.getMessage());
    }

    @Test
    public void testValidEmail() {
        User user = createUserWithDefaults();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(), "Email should be valid");
    }

    @Test
    public void testBlankEmail() {
        User user = createUserWithDefaults();
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        ConstraintViolation<User> emailViolation = violations.stream().findFirst().orElse(null);

        System.out.println(emailViolation.getMessage());

        assertEquals(1, violations.size(), "Email should not be blank");
        assertEquals("Email must not be blank", emailViolation.getMessage());
    }

    @Test
    public void testInvalidLoginWithSpaces() {
        User user = createUserWithDefaults();
        user.setLogin("login with spaces");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        ConstraintViolation<User> loginViolation = violations.stream().findFirst().orElse(null);

        System.out.println(loginViolation.getMessage());

        assertEquals(1, violations.size(), "Login should be invalid due to spaces");
        assertEquals("Login cannot contain spaces", loginViolation.getMessage());
    }

    @Test
    public void testValidLogin() {
        User user = createUserWithDefaults();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(), "Login should be valid");
    }

    @Test
    public void testBlankLogin() {
        User user = createUserWithDefaults();
        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        ConstraintViolation<User> loginViolation = violations.stream().findFirst().orElse(null);

        System.out.println(loginViolation.getMessage());

        assertEquals(1, violations.size(), "Login should not be blank");
        assertEquals("Login must not be blank", loginViolation.getMessage());
    }

    @Test
    public void testInvalidBirthdayInFuture() {
        User user = createUserWithDefaults();
        user.setBirthday(LocalDate.now().plusYears(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        ConstraintViolation<User> birthdayViolation = violations.stream().findFirst().orElse(null);

        System.out.println(birthdayViolation.getMessage());

        assertEquals(1, violations.size(), "Birthday should be invalid because it's in the future");
        assertEquals("Birthday date must be in the past", birthdayViolation.getMessage());
    }

    @Test
    public void testInvalidBirthdayInPresent() {
        User user = createUserWithDefaults();
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(), "Birthday should be invalid because it's in the present");
    }
}