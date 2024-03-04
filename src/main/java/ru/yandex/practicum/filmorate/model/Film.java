package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMin;
import ru.yandex.practicum.filmorate.validator.ValidFilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {

    private int id;
    @NotBlank(message = "Name should not be blank")
    @NotEmpty(message = "Name should not be empty")
    private String name;

    @Length(max = 200, message = "Description len must be greater than 200")
    private String description;

    // Кастомная аннотация-валидотор
    @ValidFilmReleaseDate
    private LocalDate releaseDate;
    @DurationMin(seconds = 1)
    private Duration duration;
}
