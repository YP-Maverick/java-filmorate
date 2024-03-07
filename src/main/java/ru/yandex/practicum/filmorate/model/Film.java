package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validator.ValidFilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode
public class Film {
    private int id;

    @NotBlank(message = "Имя не должно быть пустым")
    @NotEmpty(message = "Имя не должно быть пустым")
    private String name;

    @Length(max = 200, message = "Длина описания должна быть больше 200 символов")
    private String description;

    // Пользовательская аннотация валидации
    @ValidFilmReleaseDate
    private LocalDate releaseDate;

    @Positive(message = "Должно быть больше 0")
    private int duration;
}
