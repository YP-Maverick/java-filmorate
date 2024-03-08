package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReleaseDateValidator implements ConstraintValidator<ValidFilmReleaseDate, LocalDate> {

    private LocalDate firstFilmDate;

    @Override
    public void initialize(ValidFilmReleaseDate constraintAnnotation) {

        String stringDate = constraintAnnotation.fistFilmDate();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        firstFilmDate = LocalDate.from(formatter.parse(stringDate));
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // Не может быть выпущен ранее первого фильма в истории
        return value == null || !value.isBefore(firstFilmDate);
    }
}