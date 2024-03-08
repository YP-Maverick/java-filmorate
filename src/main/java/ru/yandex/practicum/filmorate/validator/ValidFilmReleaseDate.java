package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@ReportAsSingleViolation
public @interface ValidFilmReleaseDate {

    String message() default "{ru.yandex.practicum.filmorate.validator.ValidFilmReleaseDate.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String fistFilmDate();
}