package ru.yandex.practicum.filmorate.model;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    private Film createFilmWithDefaults() {
        return Film.builder()
                .id(0)
                .name("Example Film")
                .description("A sample film description")
                .releaseDate(LocalDate.now().minusYears(10))
                .duration(Duration.ofHours(2))
                .build();
    }

    @Test
    public void testBlankDescription() {
        Film film = createFilmWithDefaults();
        film.setDescription("  ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Description can be blank");
    }

    @Test
    public void testInvalidDescriptionLength() {
        Film film = createFilmWithDefaults();
        film.setDescription(Strings.repeat("f", 201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        ConstraintViolation<Film> descriptionViolation = violations.stream().findFirst().orElse(null);
        assertEquals(1, violations.size(), "Violation of restrictions more than 1");

        assertEquals("Description len must be greater than 200", descriptionViolation.getMessage());
    }

    @Test
    public void testInvalidReleaseDate() {
        Film film = createFilmWithDefaults();
        film.setReleaseDate(LocalDate.of(1700, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        ConstraintViolation<Film> releaseDateViolation = violations.stream().findFirst().orElse(null);

        assertEquals(1, violations.size(), "Violation of restrictions more than 1");
        assertEquals("Invalid film release date", releaseDateViolation.getMessage());
    }

    @Test
    public void testValidDuration() {
        Film film = createFilmWithDefaults();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size(), "Duration should be valid");
    }

    @Test
    public void testInvalidDuration() {
        Film film = createFilmWithDefaults();
        film.setDuration(Duration.ofSeconds(0));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        ConstraintViolation<Film> durationViolation = violations.stream().findFirst().orElse(null);

        assertEquals(1, violations.size(), "Violation of restrictions more than 1");
        assertEquals("must be longer than or equal to 1 second", durationViolation.getMessage());
    }
}