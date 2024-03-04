package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidFilmReleaseDate, LocalDate> {

    private static final LocalDate FIRST_FILM_DATE  = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ValidFilmReleaseDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // Не может быть выпущен ранее первого фильма в истории
        return value == null || !value.isBefore(FIRST_FILM_DATE);
    }
}